package housienariel.librarydatabase.model.dao;

import java.util.List;

import org.bson.types.ObjectId;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.Book;
import housienariel.librarydatabase.model.BooksDbException;

public interface BookDAO {

    void addBook(Book book) throws BooksDbException;
    List<Book> getAllBooks() throws BooksDbException;
    Book getBookByISBN(String ISBN) throws BooksDbException;
    void updateBook(Book book) throws BooksDbException;
    List<Book> searchBooks(String searchTerm) throws BooksDbException;
    void deleteBook(String ISBN) throws BooksDbException;
    List<Book> searchBooksByRating(int rating) throws BooksDbException;
    void addAuthorToBook(String isbn, ObjectId authorId) throws BooksDbException;
    void removeAuthorFromBook(String isbn, ObjectId authorId) throws BooksDbException;
    List<Author> getBookAuthors(String isbn) throws BooksDbException;
    List<Author> searchAuthorsByName(String name) throws BooksDbException;
}
