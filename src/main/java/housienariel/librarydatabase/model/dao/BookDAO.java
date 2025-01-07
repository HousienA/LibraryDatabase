package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Book;
import housienariel.librarydatabase.model.Rating;
import housienariel.librarydatabase.model.BooksDbException;
import java.util.List;

public interface BookDAO {

    void addBook(Book book) throws BooksDbException;
    List<Book> getAllBooks() throws BooksDbException;
    Book getBookByISBN(String ISBN) throws BooksDbException;
    void updateBook(Book book) throws BooksDbException;
    void addRatingToBook(String bookISBN, Rating rating) throws BooksDbException;
    List<Book> searchBooks(String searchTerm) throws BooksDbException;
    List<Book> searchBooksByRating(int rating) throws BooksDbException;
}
