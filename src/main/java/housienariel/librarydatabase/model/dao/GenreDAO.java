package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Genre;
import java.sql.SQLException;
import java.util.List;

public interface GenreDAO {
    void addGenre(Genre genre) throws BooksDbException;
    List<Genre> getAllGenres() throws BooksDbException;
    Genre getGenreById(int genreId) throws BooksDbException;
    void updateGenre(Genre genre) throws BooksDbException;
    void deleteGenre(int genreId) throws BooksDbException;
}