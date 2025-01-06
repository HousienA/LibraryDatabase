package housienariel.librarydatabase.model.queries;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.AuthorDAO;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class AuthorQuery implements AuthorDAO {
    private final MongoCollection<Document> authorCollection;

    public AuthorQuery() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        authorCollection = mongoClient.getDatabase("Library").getCollection("Author");
    }

    @Override
    public void addAuthor(Author author) throws BooksDbException {
        try {
            Document doc = new Document("name", author.getName())
                    .append("authorDob", author.getAuthorDob());
            authorCollection.insertOne(doc);
        } catch (Exception e) {
            throw new BooksDbException("Error adding author", e);
        }
    }

    @Override
    public List<Author> getAllAuthors() throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        try {
            for (Document doc : authorCollection.find()) {
                authors.add(new Author(
                        doc.getObjectId("_id").toString(),
                        doc.getString("name"),
                        doc.getDate("authorDob")
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error retrieving all authors", e);
        }
        return authors;
    }

    @Override
    public Author getAuthorById(String authorId) throws BooksDbException {
        try {
            Document doc = authorCollection.find(Filters.eq("_id", authorId)).first();
            if (doc != null) {
                return new Author(
                        doc.getObjectId("_id").toString(),
                        doc.getString("name"),
                        doc.getDate("authorDob")
                );
            }
        } catch (Exception e) {
            throw new BooksDbException("Error retrieving author by ID", e);
        }
        return null;
    }

    @Override
    public void updateAuthor(Author author) throws BooksDbException {
        try {
            Document updatedDoc = new Document("name", author.getName())
                    .append("authorDob", author.getAuthorDob());
            authorCollection.updateOne(Filters.eq("_id", author.getAuthorId()), new Document("$set", updatedDoc));
        } catch (Exception e) {
            throw new BooksDbException("Error updating author", e);
        }
    }

    @Override
    public void deleteAuthor(String authorId) throws BooksDbException {
        try {
            authorCollection.deleteOne(Filters.eq("_id", authorId));
        } catch (Exception e) {
            throw new BooksDbException("Error deleting author", e);
        }
    }

    @Override
    public List<Author> searchAuthorsByName(String namePattern) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        try {
            for (Document doc : authorCollection.find(Filters.regex("name", namePattern, "i"))) {
                authors.add(new Author(
                        doc.getObjectId("_id").toString(),
                        doc.getString("name"),
                        doc.getDate("authorDob")
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error searching authors by name", e);
        }
        return authors;
    }
}
