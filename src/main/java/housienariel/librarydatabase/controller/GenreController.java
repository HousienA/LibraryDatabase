package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.dao.GenreDAO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenreController {
    private static final Logger LOGGER = Logger.getLogger(GenreController.class.getName());
    private final GenreDAO genreQ;

    public GenreController(GenreDAO genreQ) {
        if (genreQ == null) {
            throw new IllegalArgumentException("Query interface cannot be null");
        }
        this.genreQ = genreQ;
    }

    public void addGenre(Integer id, String name) throws BooksDbException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be null or empty");
        }
        Genre genre = new Genre(id, name);
        try {
            genreQ.addGenre(genre);
            LOGGER.log(Level.INFO, "Genre added successfully: {0}", genre);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error adding genre", e);
            throw e;
        }
    }

    public Genre getGenreById(int genreId) throws BooksDbException {
        try {
            return genreQ.getGenreById(genreId);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving genre by ID", e);
            throw e;
        }
    }

    public void updateGenre(int genreId, String name) throws BooksDbException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Genre name cannot be null or empty");
        }
        Genre genre = new Genre(genreId, name);
        try {
            genreQ.updateGenre(genre);
            LOGGER.log(Level.INFO, "Genre updated successfully: {0}", genre);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error updating genre", e);
            throw e;
        }
    }

    public void deleteGenre(int genreId) throws BooksDbException {
        try {
            genreQ.deleteGenre(genreId);
            LOGGER.log(Level.INFO, "Genre deleted successfully: {0}", genreId);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error deleting genre", e);
            throw e;
        }
    }
    public List<Genre> getAllGenres() throws BooksDbException {
        try {
            return genreQ.getAllGenres();
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all genres", e);
            throw e;
        }
    }

}