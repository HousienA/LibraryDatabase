package housienariel.librarydatabase.controller;

import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Rating;
import housienariel.librarydatabase.model.dao.RatingDAO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RatingController {
    private static final Logger LOGGER = Logger.getLogger(RatingController.class.getName());
    private final RatingDAO ratingQ;

    public RatingController(RatingDAO ratingQ) {
        if (ratingQ == null) {
            throw new IllegalArgumentException("DAO interface cannot be null");
        }
        this.ratingQ = ratingQ;
    }

    public void addRating(Integer id, int value) throws BooksDbException {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5");
        }
        Rating rating = new Rating(id, value);
        try {
            ratingQ.addRating(rating);
            LOGGER.log(Level.INFO, "Rating added successfully: {0}", rating);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error adding rating", e);
            throw e;
        }
    }

    public Rating getRatingById(int ratingId) throws BooksDbException {
        try {
            return ratingQ.getRatingById(ratingId);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving rating by ID", e);
            throw e;
        }
    }

    public void updateRating(int ratingId, int value) throws BooksDbException {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5");
        }
        Rating rating = new Rating(ratingId, value);
        try {
            ratingQ.updateRating(rating);
            LOGGER.log(Level.INFO, "Rating updated successfully: {0}", rating);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error updating rating", e);
            throw e;
        }
    }

    public void deleteRating(int ratingId) throws BooksDbException {
        try {
            ratingQ.deleteRating(ratingId);
            LOGGER.log(Level.INFO, "Rating deleted successfully: {0}", ratingId);
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error deleting rating", e);
            throw e;
        }
    }

    public List<Rating> getAllRatings() throws BooksDbException {
        try {
            return ratingQ.getAllRatings();
        } catch (BooksDbException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all ratings", e);
            throw e;
        }
    }
}