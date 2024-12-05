package housienariel.librarydatabase.model;

import java.util.List;

public class Book {
    private String ISBN;
    private String title;
    private List<Genre> genre;
    private List<Author> authors;
    private Rating rating;

    public Book(String ISBN, String title, List<Genre> genre, List<Author> authors, Rating rating) {
        this.ISBN = ISBN;
        this.title = title;
        this.genre = genre;
        this.authors = authors;
        this.rating = rating;
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

    public List<Genre> getGenre() {
        return genre;
    }

    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
}