package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Book;
import housienariel.librarydatabase.model.BooksDbException;

import java.sql.SQLException;
import java.util.List;

public interface BookDAO {
    void addBook(Book book) throws BooksDbException;
    List<Book> getAllBooks() throws BooksDbException;
    Book getBookByISBN(String ISBN) throws BooksDbException;
    void updateBook(Book book) throws BooksDbException;
    void deleteBook(String ISBN) throws BooksDbException;
}