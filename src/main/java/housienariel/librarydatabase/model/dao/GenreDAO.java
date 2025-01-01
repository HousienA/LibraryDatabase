package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Genre;
import org.springframework.dao.DataAccessException;
import java.util.List;

public interface GenreDAO {
<<<<<<< HEAD
    void addGenre(Genre genre) throws DataAccessException;
    List<Genre> getAllGenres() throws DataAccessException;
    void deleteGenre(int genreId) throws DataAccessException;
}
=======
    void addGenre(Genre genre) throws BooksDbException;
    List<Genre> getAllGenres() throws BooksDbException;
}
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
