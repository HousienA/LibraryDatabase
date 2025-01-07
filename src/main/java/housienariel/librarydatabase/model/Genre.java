package housienariel.librarydatabase.model;

import org.bson.types.ObjectId;

public class Genre {

    private ObjectId genreId;
    private String genreName;

    // Constructor for when you already have an ObjectId
    public Genre(@SuppressWarnings("exports") ObjectId genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }

    public Genre(String genreName) {
        this.genreId = new ObjectId();
        this.genreName = genreName;
    }

    @SuppressWarnings("exports")
    public ObjectId getGenreId() {
        return genreId;
    }

    public void setGenreId(@SuppressWarnings("exports") ObjectId genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "genreId=" + genreId +
                ", genreName='" + genreName + '\'' +
                '}';
    }
}
