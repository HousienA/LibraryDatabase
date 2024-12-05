package housienariel.librarydatabase.model;

import java.util.Date;
import java.util.List;

public class Author {
    private int authorId;
    private String name;
    private Date authorDob;
    private List<Book> books;

    public Author(int authorId, String name, Date authorDob, List<Book> books) {
        this.authorId = authorId;
        this.name = name;
        this.authorDob = authorDob;
        this.books = books;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getAuthorDob() {
        return authorDob;
    }

    public void setAuthorDob(Date authorDob) {
        this.authorDob = authorDob;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}