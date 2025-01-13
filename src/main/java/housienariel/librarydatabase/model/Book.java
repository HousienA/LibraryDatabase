package housienariel.librarydatabase.model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

public class Book {
    private String ISBN;
    private String title;
    private Genre genre;
    private Rating rating;
    private List<ObjectId> authors;

    public Book(String ISBN, String title, Genre genre) {
        this.ISBN = ISBN;
        this.title = title;
        this.genre = genre;
        this.authors = new ArrayList<>();
    }

    public Book(String ISBN, String title, Genre genre, Rating rating) {
        this(ISBN, title, genre);
        this.rating = rating;
        this.authors = new ArrayList<>();
    }
    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public List<ObjectId> getAuthors() {
        return new ArrayList<>(authors);
    }

    public void setAuthors(List<ObjectId> authors) {
        this.authors = new ArrayList<>(authors);
    }

    public void addAuthor(ObjectId authorId) {
        if (!authors.contains(authorId)) {
            authors.add(authorId);
        }
    }

    public void removeAuthor(ObjectId authorId) {
        authors.remove(authorId);
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN='" + ISBN + '\'' +
                ", title='" + title + '\'' +
                ", genre=" + (genre != null ? genre.getGenreName() : "null") +
                ", rating=" + (rating != null ? rating.getRatingValue() : "null") +
                ", authors=" + authors +
                '}';
    }


}
