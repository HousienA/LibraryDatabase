package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Author;
import java.sql.SQLException;
import java.util.List;

public interface AuthorDAO {
    void addAuthor(Author author) throws SQLException;
    List<Author> getAllAuthors() throws SQLException;
    Author getAuthorById(int authorId) throws SQLException;
    void updateAuthor(Author author) throws SQLException;
    void deleteAuthor(int authorId) throws SQLException;
}