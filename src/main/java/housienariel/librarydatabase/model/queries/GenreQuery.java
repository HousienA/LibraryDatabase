package housienariel.librarydatabase.model.queries;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;

import housienariel.librarydatabase.model.BooksDbException;
import housienariel.librarydatabase.model.Genre;
import housienariel.librarydatabase.model.dao.GenreDAO;

public class GenreQuery implements GenreDAO {
    private final MongoCollection<Document> genreCollection;

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
            for (Document doc : genreCollection.find()) {
                genres.add(new Genre(
                    doc.getObjectId("_id"),
                    doc.getString("genreName")
                ));
            }
        } catch (Exception e) {
            throw new BooksDbException("Error getting all genres", e);
        }
        return genres;
    }

}
