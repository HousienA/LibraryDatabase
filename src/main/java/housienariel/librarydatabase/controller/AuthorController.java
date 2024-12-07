package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.Author;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.AuthorDAO;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthorController {
    private static final Logger LOGGER = Logger.getLogger(AuthorController.class.getName());
    private final AuthorDAO authorQ;

    public AuthorController(AuthorDAO authorQ) {
        if (authorQ == null) {
            throw new IllegalArgumentException("Query interface cannot be null");
        }
        this.authorQ = authorQ;
    }

    public void addAuthor(Integer id, String name, Date dob) throws BooksDbException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be null or empty");
        }

        Author author = new Author(id, name, dob);
        try {
            authorQ.addAuthor(author);
            LOGGER.log(Level.INFO, "Author added successfully: {0}", author);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error adding author", e);
            throw e;
        }
    }

    public Author getAuthorById(int authorId) throws BooksDbException {
        try {
            return authorQ.getAuthorById(authorId);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving author by ID", e);
            throw e;
        }
    }

    public void updateAuthor(int authorId, String name, Date dob) throws BooksDbException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be null or empty");
        }
        if (dob == null) {
            throw new IllegalArgumentException("Author date of birth cannot be null");
        }
        Author author = new Author(authorId, name, dob);
        try {
            authorQ.updateAuthor(author);
            LOGGER.log(Level.INFO, "Author updated successfully: {0}", author);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error updating author", e);
            throw e;
        }
    }

    public void deleteAuthor(int authorId) throws BooksDbException {
        try {
            authorQ.deleteAuthor(authorId);
            LOGGER.log(Level.INFO, "Author deleted successfully: {0}", authorId);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error deleting author", e);
            throw e;
        }
    }

    public List<Author> getAllAuthors() throws BooksDbException {
        try {
            return authorQ.getAllAuthors();
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all authors", e);
            throw e;
        }
    }
}