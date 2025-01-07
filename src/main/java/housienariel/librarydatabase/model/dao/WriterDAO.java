package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import java.util.List;

public interface WriterDAO {
    void addAuthorToBook(String bookISBN, Author author) throws BooksDbException;
    List<String> getBooksByAuthor(int authorId) throws BooksDbException;
    List<Author> getAuthorsForBook(String isbn) throws BooksDbException;
}
