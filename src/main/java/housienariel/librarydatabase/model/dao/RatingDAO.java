package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Rating;
import java.sql.SQLException;
import java.util.List;

public interface RatingDAO {
    void addRating(Rating rating) throws BooksDbException;
    List<Rating> getAllRatings() throws BooksDbException;
    Rating getRatingById(int ratingId) throws BooksDbException;
    void updateRating(Rating rating) throws BooksDbException;
    void deleteRating(int ratingId) throws BooksDbException;
}