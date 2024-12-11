package housienariel.librarydatabase.model.queries;

import housienariel.librarydatabase.connection.DatabaseConnection;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.dao.GenreDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreQuery implements GenreDAO {

    /**
     * @param genre the genre to add
     * @throws BooksDbException if an error occurs while adding the genre
     */
    @Override
    public void addGenre(Genre genre) throws BooksDbException {
        String query = "INSERT INTO Genre (genre_name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, genre.getGenreName());
                stmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new BooksDbException("Error adding genre", e);
            }
        } catch (SQLException e) {
            throw new BooksDbException("Transaction error", e);
        } finally {
            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                throw new BooksDbException("Error restoring auto-commit mode", e);
            }
        }
    }

    /**
     * @return a list of all genres
     * @throws BooksDbException if an error occurs while retrieving genres
     */
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

}