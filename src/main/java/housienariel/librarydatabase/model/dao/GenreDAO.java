package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.BooksDbException;
import java.util.List;

public interface GenreDAO {

    void addGenre(Genre genre) throws BooksDbException;
    List<Genre> getAllGenres() throws BooksDbException;
}
