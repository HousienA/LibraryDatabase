package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;

import java.util.List;

public interface AuthorDAO {

    void addAuthor(Author author) throws BooksDbException;
    List<Author> getAllAuthors() throws BooksDbException;
    Author getAuthorById(int authorId) throws BooksDbException;
    void updateAuthor(Author author) throws BooksDbException;
    void deleteAuthor(String authorId) throws BooksDbException;
    List<Author> searchAuthorsByName(String namePattern) throws BooksDbException;
}
