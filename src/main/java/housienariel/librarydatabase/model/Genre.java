package housienariel.librarydatabase.model;

import org.bson.types.ObjectId;

public class Genre {

    private ObjectId genreId;
    private String genreName;

    public Genre(String genreId, String genreName) {
        this.genreId = new ObjectId(genreId);
        this.genreName = genreName;
    }

    public ObjectId getGenreId() {
        return genreId;
    }

    public void setGenreId(ObjectId genreId) {
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
