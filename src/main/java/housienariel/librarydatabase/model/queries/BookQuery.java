package housienariel.librarydatabase.model.queries;

import housienariel.librarydatabase.connection.DatabaseConnection;
import housienariel.librarydatabase.model.*;
import housienariel.librarydatabase.model.dao.BookDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookQuery implements BookDAO {
    private final Connection connection;

    public BookQuery() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public void addBook(Book book) throws BooksDbException {
        String query = "INSERT INTO Book (ISBN, title, genre_id) VALUES (?, ?, ?)";
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, book.getISBN());
                pstmt.setString(2, book.getTitle());
                pstmt.setInt(3, book.getGenre().getGenreId());
                pstmt.executeUpdate();

                // Add authors if present
                if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                    addAuthorsToBook(book);
                }

                // Add rating if present
                if (book.getRating() != null) {
                    addRatingToBook(book.getISBN(), book.getRating());
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new BooksDbException("Error adding book", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error in database transaction", e);
        }
    }

    @Override
    public Book getBookByISBN(String ISBN) throws BooksDbException {
        String query = """
            SELECT b.*, g.genre_name, r.rating_value, r.rating_id
            FROM Book b
            LEFT JOIN Genre g ON b.genre_id = g.genre_id
            LEFT JOIN Rating r ON b.rating_id = r.rating_id
            WHERE b.ISBN = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, ISBN);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Book book = createBookFromResultSet(rs);
                    loadAuthorsForBook(book);
                    return book;
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error retrieving book by ISBN", e);
        }
        return null;
    }

    @Override
    public List<Book> getAllBooks() throws BooksDbException {
        List<Book> books = new ArrayList<>();
        String query = """
            SELECT b.*, g.genre_name, r.rating_value, r.rating_id
            FROM Book b
            LEFT JOIN Genre g ON b.genre_id = g.genre_id
            LEFT JOIN Rating r ON b.rating_id = r.rating_id
            """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Book book = createBookFromResultSet(rs);
                loadAuthorsForBook(book);
                books.add(book);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error retrieving all books", e);
        }
        return books;
    }

    @Override
    public void updateBook(Book book) throws BooksDbException {
        String query = "UPDATE Book SET title = ?, genre_id = ? WHERE ISBN = ?";
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, book.getTitle());
                pstmt.setInt(2, book.getGenre().getGenreId());
                pstmt.setString(3, book.getISBN());
                pstmt.executeUpdate();

                // Update authors
                updateAuthorsForBook(book);

                // Update rating if present
                if (book.getRating() != null) {
                    addRatingToBook(book.getISBN(), book.getRating());
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new BooksDbException("Error updating book", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error in database transaction", e);
        }
    }

    @Override
    public void deleteBook(String ISBN) throws BooksDbException {
        String query = "DELETE FROM Book WHERE ISBN = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, ISBN);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting book", e);
        }
    }

    @Override
    public void addRatingToBook(String bookISBN, Rating rating) throws BooksDbException {
        try {
            connection.setAutoCommit(false);

            // Check if book already has a rating
            String checkQuery = "SELECT rating_id FROM Book WHERE ISBN = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, bookISBN);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getObject("rating_id") != null) {
                        // Book already has a rating - update it
                        int existingRatingId = rs.getInt("rating_id");
                        String updateRatingQuery = "UPDATE Rating SET rating_value = ? WHERE rating_id = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateRatingQuery)) {
                            updateStmt.setInt(1, rating.getRatingValue());
                            updateStmt.setInt(2, existingRatingId);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // Book has no rating - add new one
                        RatingQuery ratingQuery = new RatingQuery();
                        ratingQuery.addRating(rating);

                        String updateBookQuery = "UPDATE Book SET rating_id = ? WHERE ISBN = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateBookQuery)) {
                            updateStmt.setInt(1, rating.getRatingId());
                            updateStmt.setString(2, bookISBN);
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new BooksDbException("Error rolling back transaction", rollbackEx);
            }
            throw new BooksDbException("Error adding rating to book", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error resetting auto-commit", e);
            }
        }
    }

    @Override
    public List<Book> searchBooks(String searchTerm) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        String query = """
        SELECT DISTINCT b.*, g.genre_name, r.rating_value, r.rating_id
        FROM Book b
        LEFT JOIN Genre g ON b.genre_id = g.genre_id
        LEFT JOIN Rating r ON b.rating_id = r.rating_id
        LEFT JOIN Writer w ON b.ISBN = w.book_ISBN
        LEFT JOIN Author a ON w.author_id = a.author_id
        WHERE b.title LIKE ? 
        OR b.ISBN LIKE ?
        OR a.name LIKE ?
        OR g.genre_name LIKE ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String pattern = "%" + searchTerm + "%";
            // Set the same search pattern for all fields
            stmt.setString(1, pattern);  // title
            stmt.setString(2, pattern);  // ISBN
            stmt.setString(3, pattern);  // author name
            stmt.setString(4, pattern);  // genre name

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = createBookFromResultSet(rs);
                    loadAuthorsForBook(book);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error searching for books", e);
        }

        return books;
    }

    @Override
    public List<Book> searchBooksByRating(int rating) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        String query = """
        SELECT DISTINCT b.*, g.genre_name, r.rating_value, r.rating_id
        FROM Book b
        LEFT JOIN Genre g ON b.genre_id = g.genre_id
        LEFT JOIN Rating r ON b.rating_id = r.rating_id
        WHERE r.rating_value = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, rating);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = createBookFromResultSet(rs);
                    loadAuthorsForBook(book);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error searching books by rating", e);
        }

        return books;
    }

    // Helper method to create a Book object from ResultSet
    private Book createBookFromResultSet(ResultSet rs) throws SQLException {
        Book book = new Book(
                rs.getString("ISBN"),
                rs.getString("title"),
                new Genre(rs.getInt("genre_id"), rs.getString("genre_name"))
        );

        // Add rating if exists
        if (rs.getObject("rating_id") != null) {
            book.setRating(new Rating(
                    rs.getInt("rating_id"),
                    rs.getInt("rating_value")
            ));
        }

        return book;
    }

    // Helper methods for implementation

    private void loadAuthorsForBook(Book book) throws SQLException {
        String authorQuery = """
            SELECT a.*
            FROM Author a
            JOIN Writer w ON a.author_id = w.author_id
            WHERE w.book_ISBN = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(authorQuery)) {
            stmt.setString(1, book.getISBN());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Author author = new Author(
                            rs.getInt("author_id"),
                            rs.getString("name"),
                            rs.getDate("author_dob")
                    );
                    book.addAuthor(author);
                }
            }
        }
    }


    private void addAuthorsToBook(Book book) throws SQLException {
        String writerQuery = "INSERT INTO Writer (book_ISBN, author_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(writerQuery)) {
            for (Author author : book.getAuthors()) {
                stmt.setString(1, book.getISBN());
                stmt.setInt(2, author.getAuthorId());
                stmt.executeUpdate();
            }
        }
    }


    private void updateAuthorsForBook(Book book) throws SQLException {
        // First remove all existing authors
        String deleteQuery = "DELETE FROM Writer WHERE book_ISBN = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setString(1, book.getISBN());
            stmt.executeUpdate();
        }

        // Then add the current authors
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            addAuthorsToBook(book);
        }
    }
}