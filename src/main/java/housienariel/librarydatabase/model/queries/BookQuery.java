package housienariel.librarydatabase.model.queries;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import housienariel.librarydatabase.model.Book;
import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.Rating;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.BookDAO;

import java.util.ArrayList;
import java.util.List;

public class BookQuery implements BookDAO {
    private final MongoCollection<Document> bookCollection;

    public BookQuery() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        bookCollection = mongoClient.getDatabase("library").getCollection("books");
    }

    /**
     * Add a book to the database with rating and genre
     *
     * @param book The book to add.
     * @throws BooksDbException If an error occurs while adding the book.
     */

    @Override
    public void addBook(Book book) throws BooksDbException {
        try {
            Document doc = new Document("ISBN", book.getISBN())
                .append("title", book.getTitle())
                .append("genreId", book.getGenre().getGenreId());
            if (book.getRating() != null) {
                doc.append("rating", book.getRating().getRatingValue());
            }
            bookCollection.insertOne(doc);
        } catch (Exception e) {
            throw new BooksDbException("Error adding book", e);
        }
    }


    /**
     * Get a book by its ISBN
     * @param ISBN The ISBN of the book to retrieve.
     * @return The book with the inserted ISBN, or null if not found.
     * @throws BooksDbException If an error occurs while retrieving the book.
     */
    @Override
    public Book getBookByISBN(String ISBN) throws BooksDbException {
<<<<<<< HEAD
        try {
            Document doc = bookCollection.find(Filters.eq("ISBN", ISBN)).first();
            if (doc != null) {
                Genre genre = new Genre(doc.getInteger("genreId"), null); // Retrieve genre details separately if needed
                Rating rating = doc.containsKey("rating") ? new Rating(null, doc.getInteger("rating")) : null;
                return new Book(
                    doc.getString("ISBN"),
                    doc.getString("title"),
                    genre,
                    rating
                );
            }
        } catch (Exception e) {
            throw new BooksDbException("Error retrieving book by ISBN", e);
=======
        String query = """
                SELECT b.*, g.genre_name, r.rating_value, r.rating_id
                FROM Book b
                LEFT JOIN Genre g ON b.genre_id = g.genre_id
                LEFT JOIN Rating r ON b.rating_id = r.rating_id
                WHERE b.ISBN = ?
                """;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, ISBN);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Book book = createBookFromResultSet(rs);
                        loadAuthorsForBook(book);
                        connection.commit();
                        return book;
                    }
                }
            } catch (SQLException e) {
                connection.rollback();
                throw new BooksDbException("Error retrieving book by ISBN", e);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Transaction error", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error restoring auto-commit mode", e);
            }
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
        }
        return null;
    }


    /**
     * @return A list of all books in the database.
     * @throws BooksDbException If an error occurs while retrieving the books.
     */
    @Override
    public List<Book> getAllBooks() throws BooksDbException {
        List<Book> books = new ArrayList<>();
<<<<<<< HEAD
        try {
            for (Document doc : bookCollection.find()) {
                Genre genre = new Genre(doc.getInteger("genreId"), null);
                Rating rating = doc.containsKey("rating") ? new Rating(null, doc.getInteger("rating")) : null;
                books.add(new Book(
                    doc.getString("ISBN"),
                    doc.getString("title"),
                    genre,
                    rating
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error retrieving all books", e);
=======
        String query = """
                SELECT b.*, g.genre_name, r.rating_value, r.rating_id
                FROM Book b
                LEFT JOIN Genre g ON b.genre_id = g.genre_id
                LEFT JOIN Rating r ON b.rating_id = r.rating_id
                """;

        try {
            connection.setAutoCommit(false);
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Book book = createBookFromResultSet(rs);
                    loadAuthorsForBook(book);
                    books.add(book);
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new BooksDbException("Error retrieving all books", e);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Transaction error", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error restoring auto-commit mode", e);
            }
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
        }
        return books;
    }


    /**
     * @param book The selected book to update.
     * @throws BooksDbException If an error occurs while updating the book.
     */
    @Override
    public void updateBook(Book book) throws BooksDbException {
        try {
<<<<<<< HEAD
            Document updatedDoc = new Document("title", book.getTitle())
                .append("genreId", book.getGenre().getGenreId());
            if (book.getRating() != null) {
                updatedDoc.append("rating", book.getRating().getRatingValue());
            }
            bookCollection.updateOne(Filters.eq("ISBN", book.getISBN()), new Document("$set", updatedDoc));
        } catch (Exception e) {
            throw new BooksDbException("Error updating book", e);
        }
    }

    @Override
    public void deleteBook(String ISBN) throws BooksDbException {
        try {
            bookCollection.deleteOne(Filters.eq("ISBN", ISBN));
        } catch (Exception e) {
            throw new BooksDbException("Error deleting book", e);
=======
            connection.setAutoCommit(false);

            System.out.println("Updating book:");
            System.out.println("ISBN: " + book.getISBN());
            System.out.println("New Title: " + book.getTitle());
            System.out.println("New Genre ID: " + book.getGenre().getGenreId());

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, book.getTitle());
                pstmt.setInt(2, book.getGenre().getGenreId());
                pstmt.setString(3, book.getISBN());

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected == 0) {
                    throw new BooksDbException("No book found with ISBN: " + book.getISBN());
                }

                if (book.getRating() != null) {
                    System.out.println("Updating rating: " + book.getRating().getRatingValue());
                    addRatingToBook(book.getISBN(), book.getRating());
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                System.out.println("SQL Error: " + e.getMessage());
                throw new BooksDbException("Error updating book", e);
            }
        } catch (SQLException e) {
            System.out.println("Transaction Error: " + e.getMessage());
            throw new BooksDbException("Error in database transaction", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error restoring auto-commit mode", e);
            }
        }
    }


    /**
     * @param bookISBN The ISBN of the book to add the rating to.
     * @param rating The rating to add.
     * @throws BooksDbException If an error occurs while adding the rating.
     */
    @Override
    public void addRatingToBook(String bookISBN, Rating rating) throws BooksDbException {
        try {
            connection.setAutoCommit(false);
            String ratingQuery = "INSERT INTO Rating (rating_value) VALUES (?)";
            int ratingId;
            try (PreparedStatement ratingStmt = connection.prepareStatement(ratingQuery, Statement.RETURN_GENERATED_KEYS)) {
                ratingStmt.setInt(1, rating.getRatingValue());
                ratingStmt.executeUpdate();

                try (ResultSet rs = ratingStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        ratingId = rs.getInt(1);
                        rating.setRatingId(ratingId);
                    } else {
                        throw new BooksDbException("Failed to get rating ID");
                    }
                }
            }

            String updateBookQuery = "UPDATE Book SET rating_id = ? WHERE ISBN = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateBookQuery)) {
                updateStmt.setInt(1, rating.getRatingId());
                updateStmt.setString(2, bookISBN);
                int updated = updateStmt.executeUpdate();
                if (updated == 0) {
                    throw new BooksDbException("Book not found with ISBN: " + bookISBN);
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new BooksDbException("Error rolling back transaction", rollbackEx);
            }
            throw new BooksDbException("Error adding rating to book: " + e.getMessage(), e);
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
        }
    }

    /**
     * Search for books by title, ISBN, author name, or genre name.
     * @param searchTerm term to match with any titles, ISBNs, author names and genre names.
     * @return A list of books matching the search.
     * @throws BooksDbException If an error occurs while searching for books.
     */
    @Override
    public List<Book> searchBooks(String searchTerm) throws BooksDbException {
        List<Book> books = new ArrayList<>();
<<<<<<< HEAD
        try {
            for (Document doc : bookCollection.find(Filters.regex("title", searchTerm, "i"))) {
                Genre genre = new Genre(doc.getInteger("genreId"), null);
                Rating rating = doc.containsKey("rating") ? new Rating(null, doc.getInteger("rating")) : null;
                books.add(new Book(
                    doc.getString("ISBN"),
                    doc.getString("title"),
                    genre,
                    rating
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error searching for books", e);
=======
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

        try {
            connection.setAutoCommit(false); // Start transaction

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                String pattern = "%" + searchTerm + "%";
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
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new BooksDbException("Error searching for books", e);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Transaction error", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error restoring auto-commit mode", e);
            }
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
        }
        return books;
    }
<<<<<<< HEAD
=======


    /**
     * Search for books by rating.
     * @param rating The rating to search for.
     * @return A list of books with the specified rating.
     * @throws BooksDbException If an error occurs while searching for books.
     */
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


    // Helper methods for implementation

    /**
     * Create a book object from a ResultSet.
     * @param rs The ResultSet to create the book from.
     * @return The book created from the ResultSet.
     * @throws SQLException If an error occurs while creating the book.
     */
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

    /**
     * @param book The book to load authors for.
     * @throws SQLException If an error occurs while loading authors.
     */
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

>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
}