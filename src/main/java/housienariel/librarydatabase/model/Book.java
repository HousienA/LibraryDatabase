package housienariel.librarydatabase.model;

import java.util.List;

public class Book {
    private String ISBN;
    private String title;
    private Genre genre;

    public Book(String ISBN, String title, Genre genre) {
        this.ISBN = ISBN;
        this.title = title;
        this.genre = genre;

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

}