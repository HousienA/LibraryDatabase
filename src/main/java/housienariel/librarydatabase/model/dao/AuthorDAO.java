package housienariel.librarydatabase.model.dao;

import java.util.List;

import org.bson.types.ObjectId;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;

public interface AuthorDAO {

    void addAuthor(Author author) throws BooksDbException;
    List<Author> getAllAuthors() throws BooksDbException;
    Author getAuthorById(@SuppressWarnings("exports") ObjectId authorId) throws BooksDbException;
    void updateAuthor(Author author) throws BooksDbException;
    void deleteAuthor(@SuppressWarnings("exports") ObjectId authorId) throws BooksDbException;
    List<Author> searchAuthorsByName(String namePattern) throws BooksDbException;
    List<Author> getBookAuthors(@SuppressWarnings("exports") ObjectId authorId) throws BooksDbException;
    void close();
}
