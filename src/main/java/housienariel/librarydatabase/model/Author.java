package housienariel.librarydatabase.model;

import java.util.Date;

import org.bson.types.ObjectId;

public class Author {

    private ObjectId authorId;
    private String name;
    private Date authorDob;
    public Author(@SuppressWarnings("exports") ObjectId authorId, String name, Date authorDob) {
        this.authorId = authorId;
        this.name = name;
        this.authorDob = authorDob;
    }

    public Author(String name) {
        this.name = name;
    }

    @SuppressWarnings("exports")
    public ObjectId getAuthorId() {
        return authorId;
    }

    public void setAuthorId(@SuppressWarnings("exports") ObjectId authorId) {
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

    @Override
    public String toString() {
        return name;
    }
}
