package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Author;
import org.springframework.dao.DataAccessException;
import housienariel.librarydatabase.model.BooksDbException;

import java.util.List;

public interface WriterDAO {
    void addAuthorToBook(String bookISBN, Author author) throws DataAccessException;
    List<String> getBooksByAuthor(int authorId) throws DataAccessException;
    List<Author> getAuthorsForBook(String isbn) throws DataAccessException;
}
