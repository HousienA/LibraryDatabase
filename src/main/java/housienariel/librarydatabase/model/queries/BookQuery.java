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
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, book.getISBN());
            pstmt.setString(2, book.getTitle());
            pstmt.setInt(3, book.getGenre().getGenreId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding book", e);
        }
    }

    @Override
    public Book getBookByISBN(String ISBN) throws BooksDbException {
        String query = "SELECT * FROM Book WHERE ISBN = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, ISBN);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Book(rs.getString("ISBN"),
                            rs.getString("title"),
                            new Genre(rs.getInt("genre_id"), null));
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
        String query = "SELECT * FROM Book";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                books.add(new Book(rs.getString("ISBN"),
                        rs.getString("title"),
                        new Genre(rs.getInt("genre_id"), null)));
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error retrieving all books", e);
        }
        return books;
    }

    @Override
    public void updateBook(Book book) throws BooksDbException {
        String query = "UPDATE Book SET title = ?, genre_id = ? WHERE ISBN = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setInt(2, book.getGenre().getGenreId());
            pstmt.setString(3, book.getISBN());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error updating book", e);
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


    public void addRatingToBook(String bookISBN, Rating rating) throws BooksDbException {
        try {
            connection.setAutoCommit(false);

            // Use RatingQuery to add the rating if it does not exist
            RatingQuery ratingQuery = new RatingQuery();
            ratingQuery.addRating(rating);

            // Update the book with the rating_id
            String updateBookQuery = "UPDATE Book SET rating_id = ? WHERE ISBN = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateBookQuery)) {
                updateStmt.setInt(1, rating.getRatingId());
                updateStmt.setString(2, bookISBN);
                updateStmt.executeUpdate();
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
}