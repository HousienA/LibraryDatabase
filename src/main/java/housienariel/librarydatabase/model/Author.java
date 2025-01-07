package housienariel.librarydatabase.model;

import org.bson.types.ObjectId;
import java.util.Date;

public class Author {

    private ObjectId authorId;
    private String name;
    private Date authorDob;
    public Author(ObjectId authorId, String name, Date authorDob) {
        this.authorId = authorId;
        this.name = name;
        this.authorDob = authorDob;
    }

    public ObjectId getAuthorId() {
        return authorId;
    }

    public void setAuthorId(ObjectId authorId) {
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
}
