package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Author;

public interface WriterDAO {
    void addAuthorToBook(String bookISBN, Author author) throws BooksDbException;
}