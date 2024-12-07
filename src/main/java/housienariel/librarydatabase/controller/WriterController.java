package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.WriterDAO;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WriterController {
    private static final Logger LOGGER = Logger.getLogger(WriterController.class.getName());
    private final WriterDAO writerQ;

    public WriterController(WriterDAO writerQ) {
        if (writerQ == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.writerQ = writerQ;
    }

    public void addAuthorToBook(String bookISBN, Author author) throws BooksDbException {
        if (bookISBN == null || bookISBN.isEmpty()) {
            throw new IllegalArgumentException("Book ISBN cannot be null or empty");
        }
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        try {
            writerQ.addAuthorToBook(bookISBN, author);
            LOGGER.log(Level.INFO, "Author added to book successfully: {0}", author);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error adding author to book", e);
            throw e;
        }
    }
}