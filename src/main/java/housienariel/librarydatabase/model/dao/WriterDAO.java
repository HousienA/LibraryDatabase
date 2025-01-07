package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;

import java.util.List;

import org.bson.types.ObjectId;

public interface WriterDAO {
    void addAuthorToBook(String bookISBN, Author author) throws BooksDbException;
    List<String> getBooksByAuthor(ObjectId authorId) throws BooksDbException;
    List<Author> getAuthorsForBook(String isbn) throws BooksDbException;
}
