package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.Book;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.Rating;
import housienariel.librarydatabase.model.dao.BookDAO;
import housienariel.librarydatabase.model.dao.GenreDAO;
import housienariel.librarydatabase.model.dao.RatingDAO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookController {
    private static final Logger LOGGER = Logger.getLogger(BookController.class.getName());
    private final BookDAO bookQ;
    private final GenreDAO genreQ;
    private final RatingDAO ratingQ;

    public BookController(BookDAO bookQ, GenreDAO genreQ, RatingDAO ratingQ) {
        if (bookQ == null || genreQ == null || ratingQ == null) {
            throw new IllegalArgumentException("DAO interface cannot be null");
        }
        this.bookQ = bookQ;
        this.genreQ = genreQ;
        this.ratingQ = ratingQ;
    }

    public void addBook(String title, String isbn, int genreId) throws BooksDbException {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be null or empty");
        }
        if (isbn == null || isbn.isEmpty()) {
            throw new IllegalArgumentException("Book ISBN cannot be null or empty");
        }
        Book book = new Book(isbn, title, new Genre(genreId, null));
        try {
            bookQ.addBook(book);
            LOGGER.log(Level.INFO, "Book added successfully: {0}", book);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error adding book", e);
            throw e;
        }
    }

    public Book getBook(String isbn) throws BooksDbException {
        if (isbn == null || isbn.isEmpty()) {
            throw new IllegalArgumentException("Book ISBN cannot be null or empty");
        }
        try {
            return bookQ.getBookByISBN(isbn);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving book by ISBN", e);
            throw e;
        }
    }

    public void updateBook(String title, String isbn, int genreId) throws BooksDbException {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be null or empty");
        }
        if (isbn == null || isbn.isEmpty()) {
            throw new IllegalArgumentException("Book ISBN cannot be null or empty");
        }
        Book book = new Book(isbn, title, new Genre(genreId, null));
        try {
            bookQ.updateBook(book);
            LOGGER.log(Level.INFO, "Book updated successfully: {0}", book);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error updating book", e);
            throw e;
        }
    }

    public void deleteBook(String isbn) throws BooksDbException {
        if (isbn == null || isbn.isEmpty()) {
            throw new IllegalArgumentException("Book ISBN cannot be null or empty");
        }
        try {
            bookQ.deleteBook(isbn);
            LOGGER.log(Level.INFO, "Book deleted successfully: {0}", isbn);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error deleting book", e);
            throw e;
        }
    }

    public List<Book> getAllBooks() throws BooksDbException {
        try {
            return bookQ.getAllBooks();
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all books", e);
            throw e;
        }
    }

    public void addRatingToBook(Integer id, int ratingValue, String isbn) throws BooksDbException {
        if (isbn == null || isbn.isEmpty()) {
            throw new IllegalArgumentException("Book ISBN cannot be null or empty");
        }
        if (ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5");
        }
        Rating rating = new Rating(id, ratingValue);
        try {
            bookQ.addRatingToBook(isbn, rating);
            LOGGER.log(Level.INFO, "Rating added to book successfully: {0}", rating);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error adding rating to book", e);
            throw e;
        }
    }
}