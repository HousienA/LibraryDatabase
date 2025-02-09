package housienariel.librarydatabase.model.queries;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import housienariel.librarydatabase.model.Author;
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
            Document query = new Document("ISBN", ISBN);
            Document doc = bookCollection.find(query).first();
            if (doc != null) {
                Genre genre = new Genre(doc.getObjectId("genre_id"), null);
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
                Genre genre = new Genre(doc.getObjectId("genre_id"), null);
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
            Document query = new Document("ISBN", book.getISBN());
            Document updatedDoc = new Document("title", book.getTitle())
                    .append("genre_id", book.getGenre().getGenreId());
            if (book.getRating() != null) {
                updatedDoc.append("rating", book.getRating().getRatingValue());
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
        List<Author> authors = new ArrayList<>();
        try {
            Document bookQuery = new Document("title", new Document("$regex", searchTerm).append("$options", "i"));
            for (Document doc : bookCollection.find(bookQuery)) {
                Genre genre = new Genre(doc.getObjectId("genre_id"), null);
                Rating rating = doc.containsKey("rating") ? new Rating(null, doc.getInteger("rating")) : null;
                books.add(new Book(
                        doc.getString("ISBN"),
                        doc.getString("title"),
                        genre,
                        rating
                ));
            }

            authors = searchAuthorsByName(searchTerm);

            if (!authors.isEmpty()) {
                System.out.println("Authors matching search term: ");
                authors.forEach(author -> System.out.println(author.getName()));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error searching for books and authors", e);
        }
        return books;
    }


    @Override
    public List<Book> searchBooksByRating(int rating) throws BooksDbException {
        List<Book> books = new ArrayList<>();
        try {
            Document query = new Document("rating", rating);
            for (Document doc : bookCollection.find(query)) {
                Genre genre = new Genre(doc.getObjectId("genre_id"), null);
                Rating ratingObj = doc.containsKey("rating") ? new Rating(null, doc.getInteger("rating")) : null;
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

    @Override
    public void addAuthorToBook(String isbn, ObjectId authorId) throws BooksDbException {
        try {
            Document bookQuery = new Document("ISBN", isbn);
            Document updateBook = new Document("$push", new Document("author_ids", authorId));
            bookCollection.updateOne(bookQuery, updateBook);

            Document authorQuery = new Document("_id", authorId);
            Document updateAuthor = new Document("$push", new Document("book_ids", bookQuery.get("_id")));
            bookCollection.updateOne(authorQuery, updateAuthor);

        } catch (Exception e) {
            throw new BooksDbException("Error adding author to book: " + e.getMessage(), e);
        }
    }



    @Override
    public void removeAuthorFromBook(String isbn, ObjectId authorId) throws BooksDbException {
        try {
            Document bookQuery = new Document("ISBN", isbn);
            Document updateBook = new Document("$pull", new Document("author_ids", authorId));
            bookCollection.updateOne(bookQuery, updateBook);

            Document authorQuery = new Document("_id", authorId);
            Document updateAuthor = new Document("$pull", new Document("book_ids", bookQuery.get("_id")));
            bookCollection.updateOne(authorQuery, updateAuthor);

        } catch (Exception e) {
            throw new BooksDbException("Error removing author from book: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Author> getBookAuthors(String isbn) throws BooksDbException {
        try {
            Document query = new Document("ISBN", isbn);
            Document book = bookCollection.find(query).first();

            if (book != null && book.containsKey("author_ids")) {
                List<ObjectId> authorIds = book.getList("author_ids", ObjectId.class);

                List<Author> authors = new ArrayList<>();
                for (ObjectId authorId : authorIds) {
                    Document authorQuery = new Document("_id", authorId);
                    Document authorDoc = bookCollection.find(authorQuery).first();

                    if (authorDoc != null) {
                        String name = authorDoc.getString("name");
                        authors.add(new Author(authorId, name));
                    }
                }
                return authors;
            }

            return new ArrayList<>();
        } catch (Exception e) {
            throw new BooksDbException("Error getting book authors: " + e.getMessage(), e);
        }
    }


    @Override
    public List<Author> searchAuthorsByName(String name) throws BooksDbException {
        List<Author> authors = new ArrayList<>();
        try {
            Document query = new Document("name", new Document("$regex", name).append("$options", "i"));
            for (Document doc : bookCollection.find(query)) {
                String authorName = doc.getString("name");
                authors.add(new Author(authorName));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error searching authors by name: " + e.getMessage(), e);
        }
        return authors;
    }
}