package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Author;
import org.springframework.dao.DataAccessException;
import java.util.List;

public interface AuthorDAO {
<<<<<<< HEAD
    void addAuthor(Author author) throws DataAccessException;
    List<Author> getAllAuthors() throws DataAccessException;
    Author getAuthorById(int authorId) throws DataAccessException;
    void updateAuthor(Author author) throws DataAccessException;
    void deleteAuthor(int authorId) throws DataAccessException;
    List<Author> searchAuthorsByName(String namePattern) throws DataAccessException;
}
=======
    void addAuthor(Author author) throws BooksDbException;
    List<Author> getAllAuthors() throws BooksDbException;
    Author getAuthorById(int authorId) throws BooksDbException;
    void updateAuthor(Author author) throws BooksDbException;
    List<Author> searchAuthorsByName(String namePattern) throws BooksDbException;
}
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
