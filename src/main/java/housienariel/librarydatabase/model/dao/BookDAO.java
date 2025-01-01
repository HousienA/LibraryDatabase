package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Book;
import housienariel.librarydatabase.model.Rating;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface BookDAO {
    void addBook(Book book) throws DataAccessException;
    List<Book> getAllBooks() throws DataAccessException;
    Book getBookByISBN(String ISBN) throws DataAccessException;
    void updateBook(Book book) throws DataAccessException;
    void deleteBook(String ISBN) throws DataAccessException;
    void addRatingToBook(String bookISBN, Rating rating) throws DataAccessException;
    List<Book> searchBooks(String searchTerm) throws DataAccessException;
    List<Book> searchBooksByRating(int rating) throws DataAccessException;
}
