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

    @Override
    public Book getBookByISBN(String ISBN) throws BooksDbException {
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
        }
        return null;
    }

    @Override
    public List<Book> getAllBooks() throws BooksDbException {
        List<Book> books = new ArrayList<>();
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
        }
        return books;
    }

    @Override
    public void updateBook(Book book) throws BooksDbException {
        try {
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
        }
    }

    @Override
    public List<Book> searchBooks(String searchTerm) throws BooksDbException {
        List<Book> books = new ArrayList<>();
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
        }
        return books;
    }
}