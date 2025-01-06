package housienariel.librarydatabase.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "Book")
public class Book {
    private String ISBN;
    private String title;
    private Genre genre;
    private Rating rating;
    private List<Author> authors;

    public Book(String ISBN, String title, Genre genre) {
        this.ISBN = ISBN;
        this.title = title;
        this.genre = genre;
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

    public List<Author> getAuthors() {
        return new ArrayList<>(authors);
    }

    public void setAuthors(List<Author> authors) {
        this.authors = new ArrayList<>(authors);
    }

    public void addAuthor(Author author) {
        if (!authors.contains(author)) {
            authors.add(author);
        }
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN='" + ISBN + '\'' +
                ", title='" + title + '\'' +
                ", genre=" + (genre != null ? genre.getGenreName() : "null") +
                ", rating=" + (rating != null ? rating.getRatingValue() : "null") +
                ", authors=" + authors.stream().map(Author::getName).toList() +
                '}';
    }

    // Override equals() for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return ISBN.equals(book.ISBN);
    }
}
