package housienariel.librarydatabase.model.queries;

import housienariel.librarydatabase.connection.DatabaseConnection;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Rating;
import housienariel.librarydatabase.model.dao.RatingDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingQuery implements RatingDAO {
    private final Connection connection;

    public RatingQuery() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

        //TODO: Remove the ability to add multiple ratings to the same book

    @Override
    public void addRating(Rating rating) throws BooksDbException {
        String query = "INSERT INTO Rating (rating_value) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, rating.getRatingValue());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding rating", e);
        }
    }

    @Override
    public List<Rating> getAllRatings() throws BooksDbException {
        List<Rating> ratings = new ArrayList<>();
        String query = "SELECT * FROM Rating";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Rating rating = new Rating(rs.getInt("rating_id"), rs.getInt("rating_value"));
                ratings.add(rating);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error retrieving all ratings", e);
        }
        return ratings;
    }

    @Override
    public Rating getRatingById(int ratingId) throws BooksDbException {
        String query = "SELECT * FROM Rating WHERE rating_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ratingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Rating(rs.getInt("rating_id"), rs.getInt("rating_value"));
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error retrieving rating by ID", e);
        }
        return null;
    }

    @Override
    public void updateRating(Rating rating) throws BooksDbException {
        String query = "UPDATE Rating SET rating_value = ? WHERE rating_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, rating.getRatingValue());
            stmt.setInt(2, rating.getRatingId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error updating rating", e);
        }
    }

    @Override
    public void deleteRating(int ratingId) throws BooksDbException {
        String query = "DELETE FROM Rating WHERE rating_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, ratingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting rating", e);
        }
    }
}