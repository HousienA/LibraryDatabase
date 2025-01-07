package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;

import java.util.List;

import org.bson.types.ObjectId;

public interface AuthorDAO {

    void addAuthor(Author author) throws BooksDbException;
    List<Author> getAllAuthors() throws BooksDbException;
    Author getAuthorById(ObjectId authorId) throws BooksDbException;
    void updateAuthor(Author author) throws BooksDbException;
    void deleteAuthor(ObjectId authorId) throws BooksDbException;
    List<Author> searchAuthorsByName(String namePattern) throws BooksDbException;
}
