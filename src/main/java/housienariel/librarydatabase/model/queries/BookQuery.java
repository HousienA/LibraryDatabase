package housienariel.librarydatabase.model.queries;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import housienariel.librarydatabase.model.Book;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.Rating;
import housienariel.librarydatabase.model.dao.BookDAO;

public class BookQuery implements BookDAO {
    private final MongoCollection<Document> bookCollection;

    public BookQuery() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        bookCollection = mongoClient.getDatabase("Library").getCollection("Book");
    }

    @Override
    public void addBook(Book book) throws BooksDbException {
        try {
            Document doc = new Document("ISBN", book.getISBN())
                    .append("title", book.getTitle())
                    .append("genre_id", book.getGenre().getGenreId());
            if (book.getRating() != null) {
                doc.append("rating_id", book.getRating().getRatingValue());
            }
            bookCollection.insertOne(doc);
        } catch (Exception e) {
            throw new BooksDbException("Error adding book", e);
        }
    }

    @Override
    public Book getBookByISBN(String ISBN) throws BooksDbException {
        try {
            Document query = new Document("ISBN", ISBN);
            Document doc = bookCollection.find(query).first();
            if (doc != null) {
                Genre genre = new Genre(doc.getObjectId("genre_id"), null);
                Rating rating = doc.containsKey("rating_id") ? new Rating(null, doc.getInteger("rating_id")) : null;
                return new Book(
                        doc.getString("ISBN"),
                        doc.getString("title"),
                        genre,
                        rating
                );
            }
        } catch (Exception e) {
            throw new BooksDbException("Error retrieving book by ISBN", e);
        }
        return null;
    }

    @Override
    public List<Book> getAllBooks() throws BooksDbException {
        List<Book> books = new ArrayList<>();
        try {
            for (Document doc : bookCollection.find()) {
                Genre genre = new Genre(doc.getObjectId("genre_id"), null);
                Rating rating = doc.containsKey("rating_id") ? new Rating(null, doc.getInteger("rating_id")) : null;
                books.add(new Book(
                        doc.getString("ISBN"),
                        doc.getString("title"),
                        genre,
                        rating
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error retrieving all books", e);
        }
        return books;
    }

    @Override
    public void updateBook(Book book) throws BooksDbException {
        try {
            Document query = new Document("ISBN", book.getISBN());
            Document updatedDoc = new Document("title", book.getTitle())
                    .append("genre_id", book.getGenre().getGenreId());  // Uses ObjectId correctly
            if (book.getRating() != null) {
                updatedDoc.append("rating_id", book.getRating().getRatingValue());
            }
            bookCollection.updateOne(query, new Document("$set", updatedDoc));
        } catch (Exception e) {
            throw new BooksDbException("Error updating book", e);
        }
    }

    @Override
    public void deleteBook(String ISBN) throws BooksDbException {
        try {
            Document query = new Document("ISBN", ISBN);
            bookCollection.deleteOne(query);
        } catch (Exception e) {
            throw new BooksDbException("Error deleting book", e);
        }
    }

    @Override
    public List<Book> searchBooks(String searchTerm) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        try {
            Document query = new Document("title", new Document("$regex", searchTerm).append("$options", "i"));
            for (Document doc : bookCollection.find(query)) {
                Genre genre = new Genre(doc.getObjectId("genre_id"), null);
                Rating rating = doc.containsKey("rating_id") ? new Rating(null, doc.getInteger("rating_id")) : null;
                books.add(new Book(
                        doc.getString("ISBN"),
                        doc.getString("title"),
                        genre,
                        rating
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error searching for books", e);
        }
        return books;
    }

        @Override
    public List<Book> searchBooksByRating(int rating) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        try {
            Document query = new Document("rating_id", rating);
            for (Document doc : bookCollection.find(query)) {
                Genre genre = new Genre(doc.getObjectId("genre_id"), null);
                Rating ratingObj = doc.containsKey("rating") ? new Rating(null, doc.getInteger("rating_id")) : null;
                books.add(new Book(
                        doc.getString("ISBN"),
                        doc.getString("title"),
                        genre,
                        ratingObj
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error searching for books by rating", e);
        }
        return books;
}

}
