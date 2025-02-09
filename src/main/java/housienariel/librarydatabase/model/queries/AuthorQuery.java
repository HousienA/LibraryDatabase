package housienariel.librarydatabase.model.queries;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.AuthorDAO;

public class AuthorQuery implements AuthorDAO {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> authorCollection;
    private final MongoCollection<Document> bookCollection;

    public AuthorQuery() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        authorCollection = mongoClient.getDatabase("Library").getCollection("Author");
        bookCollection = mongoClient.getDatabase("Library").getCollection("Book");
    }

    @Override
    public void addAuthor(Author author) throws BooksDbException {
        try {
            Document doc = new Document("name", author.getName())
                    .append("author_dob", author.getAuthorDob());
            authorCollection.insertOne(doc);
            author.setAuthorId(doc.getObjectId("_id"));
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
                        doc.getObjectId("_id"),
                        doc.getString("name"),
                        doc.getDate("author_dob")
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error retrieving all authors: " + e.getMessage(), e);
        }
        return authors;
    }

    @Override
    public Author getAuthorById(@SuppressWarnings("exports") ObjectId authorId) throws BooksDbException {
        try {
            Document query = new Document("_id", authorId);
            Document doc = authorCollection.find(query).first();

            if (doc != null) {
                return new Author(
                        doc.getObjectId("_id"),
                        doc.getString("name"),
                        doc.getDate("author_dob")
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
            Document query = new Document("_id", author.getAuthorId());
            Document updatedDoc = new Document("name", author.getName())
                    .append("author_dob", author.getAuthorDob());
            Document updateOperation = new Document("$set", updatedDoc);
            authorCollection.updateOne(query, updateOperation);
        } catch (Exception e) {
            throw new BooksDbException("Error updating author: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAuthor(ObjectId authorId) throws BooksDbException {
        try {
            Document update = new Document("$pull",
                new Document("author_id", authorId));
            bookCollection.updateMany(
                new Document("author_id", authorId),
                update
            );

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
            Document query = new Document("name",
                new Document("$regex", namePattern)
                    .append("$options", "i"));
            for (Document doc : authorCollection.find(query)) {
                authors.add(new Author(
                        doc.getObjectId("_id"),
                        doc.getString("name"),
                        doc.getDate("author_dob")
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error searching authors by name: " + e.getMessage(), e);
        }
        return authors;
    }

    @Override
    public List<String> getAuthorsBooks(@SuppressWarnings("exports") ObjectId authorId) throws BooksDbException {
        try {
            List<String> bookIsbns = new ArrayList<>();
            Document query = new Document("author_ids", authorId);
            for (Document bookDoc : bookCollection.find(query)) {
                bookIsbns.add(bookDoc.getString("ISBN"));
            }
            return bookIsbns;
        } catch (Exception e) {
            throw new BooksDbException("Error getting books for author: " + e.getMessage(), e);
        }
    }


    @Override
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}