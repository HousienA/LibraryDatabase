package housienariel.librarydatabase.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "Author")
public class Author {
    @Id
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
