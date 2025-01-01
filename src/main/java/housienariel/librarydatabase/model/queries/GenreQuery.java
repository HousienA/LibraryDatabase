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
        }
    }

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

    @Override
    public void deleteGenre(String genreId) throws BooksDbException {
        try {
            genreCollection.deleteOne(Filters.eq("_id", genreId));
        } catch (Exception e) {
            throw new BooksDbException("Error deleting genre", e);
        }
    }
}