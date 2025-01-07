package housienariel.librarydatabase.model.queries;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.AuthorDAO;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class AuthorQuery implements AuthorDAO {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> authorCollection;

    public AuthorQuery() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        authorCollection = mongoClient.getDatabase("Library").getCollection("Author");
    }

    @Override
    public void addAuthor(Author author) throws BooksDbException {
        try {
            Document doc = new Document("name", author.getName())
                    .append("authorDob", author.getAuthorDob());
            authorCollection.insertOne(doc);
        } catch (Exception e) {
            throw new BooksDbException("Error adding author: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Author> getAllAuthors() throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        try {
            for (Document doc : authorCollection.find()) {
                authors.add(new Author(
                        doc.getObjectId("_id"), // Using ObjectId directly
                        doc.getString("name"),
                        doc.getDate("authorDob")
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error retrieving all authors: " + e.getMessage(), e);
        }
        return authors;
    }

    @Override
    public Author getAuthorById(ObjectId authorId) throws BooksDbException {
        try {
            Document query = new Document("_id", authorId);
            Document doc = authorCollection.find(query).first();

            if (doc != null) {
                return new Author(
                        doc.getObjectId("_id"), // Using ObjectId directly
                        doc.getString("name"),
                        doc.getDate("authorDob")
                );
            }
        } catch (Exception e) {
            throw new BooksDbException("Error retrieving author by ID: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void updateAuthor(Author author) throws BooksDbException {
        try {
            Document query = new Document("_id", author.getAuthorId()); // Assuming author.getAuthorId() returns ObjectId
            Document updatedDoc = new Document("name", author.getName())
                    .append("authorDob", author.getAuthorDob());
            Document updateOperation = new Document("$set", updatedDoc);
            authorCollection.updateOne(query, updateOperation);
        } catch (Exception e) {
            throw new BooksDbException("Error updating author: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAuthor(ObjectId authorId) throws BooksDbException {
        try {
            Document query = new Document("_id", authorId);
            authorCollection.deleteOne(query);
        } catch (Exception e) {
            throw new BooksDbException("Error deleting author: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Author> searchAuthorsByName(String namePattern) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        try {
            Document query = new Document("name", new Document("$regex", namePattern).append("$options", "i"));
            for (Document doc : authorCollection.find(query)) {
                authors.add(new Author(
                        doc.getObjectId("_id"), // Using ObjectId directly
                        doc.getString("name"),
                        doc.getDate("authorDob")
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error searching authors by name: " + e.getMessage(), e);
        }
        return authors;
    }

    public void close() {
        mongoClient.close();
    }
}
