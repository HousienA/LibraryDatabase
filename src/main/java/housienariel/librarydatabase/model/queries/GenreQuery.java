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
    private final MongoCollection<Document> genreCollection;

    // MongoDB client initialization
    public GenreQuery() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        genreCollection = mongoClient.getDatabase("library").getCollection("genres");
    }

    /**
     * Add a genre to the database
     * @param genre the genre to add
     * @throws BooksDbException if an error occurs while adding the genre
     */
    @Override
    public void addGenre(Genre genre) throws BooksDbException {
        try {
            // Creating a MongoDB document for the genre
            Document doc = new Document("genreName", genre.getGenreName());
            genreCollection.insertOne(doc);
        } catch (Exception e) {
            throw new BooksDbException("Error adding genre", e);
        }
    }

    /**
     * Retrieve all genres from the database
     * @return a list of all genres
     * @throws BooksDbException if an error occurs while retrieving genres
     */
    @Override
    public List<Genre> getAllGenres() throws BooksDbException {
        List<Genre> genres = new ArrayList<>();
        try {
            // Iterate through MongoDB collection and create Genre objects
            for (Document doc : genreCollection.find()) {
                genres.add(new Genre(
                    doc.getObjectId("_id").toString(),  // Assuming genre has an _id field as a MongoDB ObjectId
                    doc.getString("genreName")
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error getting all genres", e);
        }
        return genres;
    }

    /**
     * Delete a genre by its ID
     * @param genreId the ID of the genre to delete
     * @throws BooksDbException if an error occurs while deleting the genre
     */
    @Override
    public void deleteGenre(String genreId) throws BooksDbException {
        try {
            // Delete genre from MongoDB by its _id
            genreCollection.deleteOne(Filters.eq("_id", genreId));
        } catch (Exception e) {
            throw new BooksDbException("Error deleting genre", e);
        }
    }
}
