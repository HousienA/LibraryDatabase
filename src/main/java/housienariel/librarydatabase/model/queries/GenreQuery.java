package housienariel.librarydatabase.model.queries;

import housienariel.librarydatabase.connection.DatabaseConnection;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.dao.GenreDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreQuery implements GenreDAO {
    @Override
    public void addGenre(Genre genre) throws BooksDbException {
        String query = "INSERT INTO Genre (genre_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, genre.getGenreName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error adding genre", e);
        }
    }

    @Override
    public List<Genre> getAllGenres() throws BooksDbException {
        List<Genre> genres = new ArrayList<>();
        String query = "SELECT * FROM Genre";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
                genres.add(genre);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error getting all genres", e);
        }
        return genres;
    }

    @Override
    public Genre getGenreById(int genreId) throws BooksDbException {
        String query = "SELECT * FROM Genre WHERE genre_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, genreId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
                }
            }
        } catch (SQLException e) {
            throw new BooksDbException("Error getting genre by ID", e);
        }
        return null;
    }

    @Override
    public void updateGenre(Genre genre) throws BooksDbException {
        String query = "UPDATE Genre SET genre_name = ? WHERE genre_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, genre.getGenreName());
            stmt.setInt(2, genre.getGenreId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error updating genre", e);
        }
    }

    @Override
    public void deleteGenre(int genreId) throws BooksDbException {
        String query = "DELETE FROM Genre WHERE genre_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, genreId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new BooksDbException("Error deleting genre", e);
        }
    }
}