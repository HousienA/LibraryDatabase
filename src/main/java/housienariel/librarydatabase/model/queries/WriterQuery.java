package housienariel.librarydatabase.model.queries;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.WriterDAO;

import java.util.ArrayList;
import java.util.List;

public class WriterQuery implements WriterDAO {
    private final MongoCollection<Document> writerCollection;

    public WriterQuery() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        writerCollection = mongoClient.getDatabase("library").getCollection("writers");
    }

    /**
     * @param bookISBN the ISBN of the book
     * @param author the author to add
     * @throws BooksDbException if an error occurs while adding the author to the book
     */
    @Override
    public void addAuthorToBook(String bookISBN, Author author) throws BooksDbException {
        try {
            Document doc = new Document("bookISBN", bookISBN)
                .append("authorName", author.getName());
            writerCollection.insertOne(doc);
        } catch (Exception e) {
            throw new BooksDbException("Error adding author to book", e);
        }
    }


    /**
     * @param isbn the ISBN of the book
     * @return a list of authors for the book
     * @throws BooksDbException if an error occurs while retrieving authors for the book
     */
    @Override
    public List<Author> getAuthorsForBook(String isbn) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
<<<<<<< HEAD
        try {
            for (Document doc : writerCollection.find(Filters.eq("bookISBN", isbn))) {
                authors.add(new Author(
                    null, // No ID available unless authors are referenced
                    doc.getString("authorName"),
                    null
                ));
            }
        } catch (Exception e) {
=======
        String query = """
        SELECT a.*
        FROM Author a
        JOIN Writer w ON a.author_id = w.author_id
        WHERE w.book_ISBN = ?
        """;

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, isbn);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Author author = new Author(
                                rs.getInt("author_id"),
                                rs.getString("name"),
                                rs.getDate("author_dob")
                        );
                        authors.add(author);
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
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
            throw new BooksDbException("Error getting authors for book", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error resetting auto-commit", e);
            }
        }
        return authors;
    }


    /**
     * @param authorId the ID of the author
     * @return a list of books by the author
     * @throws BooksDbException if an error occurs while retrieving books by the author
     */
    @Override
    public List<String> getBooksByAuthor(String authorName) throws BooksDbException {
        List<String> bookISBNs = new ArrayList<>();
<<<<<<< HEAD
        try {
            for (Document doc : writerCollection.find(Filters.eq("authorName", authorName))) {
                bookISBNs.add(doc.getString("bookISBN"));
            }
        } catch (Exception e) {
=======
        String query = "SELECT book_ISBN FROM Writer WHERE author_id = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, authorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        bookISBNs.add(rs.getString("book_ISBN"));
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
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
            throw new BooksDbException("Error getting books for author", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error resetting auto-commit", e);
            }
        }
        return bookISBNs;
    }
}
