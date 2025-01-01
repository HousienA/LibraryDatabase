package housienariel.librarydatabase.model.queries;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.dao.GenreDAO;

import java.util.ArrayList;
import java.util.List;

public class GenreQuery implements GenreDAO {
<<<<<<< HEAD
    private final MongoCollection<Document> genreCollection;

    public GenreQuery() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        genreCollection = mongoClient.getDatabase("library").getCollection("genres");
    }

    @Override
    public void addGenre(Genre genre) throws BooksDbException {
        try {
            Document doc = new Document("genreName", genre.getGenreName());
            genreCollection.insertOne(doc);
        } catch (Exception e) {
            throw new BooksDbException("Error adding genre", e);
=======

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
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
        }
    }

    /**
     * @return a list of all genres
     * @throws BooksDbException if an error occurs while retrieving genres
     */
    @Override
    public List<Genre> getAllGenres() throws BooksDbException {
        List<Genre> genres = new ArrayList<>();
        try {
            for (Document doc : genreCollection.find()) {
                genres.add(new Genre(
                    doc.getObjectId("_id").toString(),
                    doc.getString("genreName")
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error getting all genres", e);
        }
        return genres;
    }

<<<<<<< HEAD
    @Override
    public void deleteGenre(String genreId) throws BooksDbException {
        try {
            genreCollection.deleteOne(Filters.eq("_id", genreId));
        } catch (Exception e) {
            throw new BooksDbException("Error deleting genre", e);
        }
    }
=======
>>>>>>> 09cf76f10e6ce1acbc891d42a98dfced8ca6e6a8
}