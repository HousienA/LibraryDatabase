package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Genre;
import org.springframework.dao.DataAccessException;
import java.util.List;

public interface GenreDAO {
    void addGenre(Genre genre) throws DataAccessException;
    List<Genre> getAllGenres() throws DataAccessException;
    void deleteGenre(int genreId) throws DataAccessException;
}
