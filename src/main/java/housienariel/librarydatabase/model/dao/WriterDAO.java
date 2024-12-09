package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Author;

import java.util.List;

public interface WriterDAO {
    void addAuthorToBook(String bookISBN, Author author) throws BooksDbException;
    void removeAuthorFromBook(String bookISBN, int authorId) throws BooksDbException;
    List<String> getBooksByAuthor(int authorId) throws BooksDbException;
}