package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Author;
import org.springframework.dao.DataAccessException;
import java.util.List;

public interface AuthorDAO {
    void addAuthor(Author author) throws DataAccessException;
    List<Author> getAllAuthors() throws DataAccessException;
    Author getAuthorById(int authorId) throws DataAccessException;
    void updateAuthor(Author author) throws DataAccessException;
    void deleteAuthor(int authorId) throws DataAccessException;
    List<Author> searchAuthorsByName(String namePattern) throws DataAccessException;
}
