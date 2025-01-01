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
     * Add an author to a book
     * @param bookISBN the ISBN of the book
     * @param author the author to add
     * @throws BooksDbException if an error occurs while adding the author to the book
     */
    @Override
    public void addAuthorToBook(String bookISBN, Author author) throws BooksDbException {
        try {
            // Insert the author and the book association into MongoDB
            Document doc = new Document("bookISBN", bookISBN)
                .append("authorName", author.getName());
            writerCollection.insertOne(doc);
        } catch (Exception e) {
            throw new BooksDbException("Error adding author to book", e);
        }
    }

    /**
     * Get the authors for a specific book by ISBN
     * @param isbn the ISBN of the book
     * @return a list of authors for the book
     * @throws BooksDbException if an error occurs while retrieving authors for the book
     */
    @Override
    public List<Author> getAuthorsForBook(String isbn) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        try {
            // Find all documents where the bookISBN matches the given ISBN
            for (Document doc : writerCollection.find(Filters.eq("bookISBN", isbn))) {
                // Create Author objects for each associated author
                authors.add(new Author(
                    null, // No ID available unless authors are referenced
                    doc.getString("authorName"),
                    null  // Assuming no DOB or other author data in this collection
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error getting authors for book", e);
        }
        return authors;
    }

    /**
     * Get the list of books written by a specific author
     * @param authorName the name of the author
     * @return a list of book ISBNs written by the author
     * @throws BooksDbException if an error occurs while retrieving books for the author
     */
    @Override
    public List<String> getBooksByAuthor(String authorName) throws BooksDbException {
        List<String> bookISBNs = new ArrayList<>();
        try {
            // Find all documents where the authorName matches the given author's name
            for (Document doc : writerCollection.find(Filters.eq("authorName", authorName))) {
                // Extract bookISBN from each document and add it to the list
                bookISBNs.add(doc.getString("bookISBN"));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error getting books for author", e);
        }
        return bookISBNs;
    }
}
