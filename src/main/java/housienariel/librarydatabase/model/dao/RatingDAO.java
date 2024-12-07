package housienariel.librarydatabase.model.dao;

import housienariel.librarydatabase.model.Rating;
import java.sql.SQLException;
import java.util.List;

public interface RatingDAO {
    void addRating(Rating rating) throws SQLException;
    List<Rating> getAllRatings() throws SQLException;
    Rating getRatingById(int ratingId) throws SQLException;
    void updateRating(Rating rating) throws SQLException;
    void deleteRating(int ratingId) throws SQLException;
}