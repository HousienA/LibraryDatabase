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

    @Override
    public List<Author> getAuthorsForBook(String isbn) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        try {
            for (Document doc : writerCollection.find(Filters.eq("bookISBN", isbn))) {
                authors.add(new Author(
                    null, // No ID available unless authors are referenced
                    doc.getString("authorName"),
                    null
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error getting authors for book", e);
        }
        return authors;
    }

    @Override
    public List<String> getBooksByAuthor(String authorName) throws BooksDbException {
        List<String> bookISBNs = new ArrayList<>();
        try {
            for (Document doc : writerCollection.find(Filters.eq("authorName", authorName))) {
                bookISBNs.add(doc.getString("bookISBN"));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error getting books for author", e);
        }
        return bookISBNs;
    }
}
