package housienariel.librarydatabase.model;

import java.util.Date;
import java.util.List;

public class Author {
    private Integer authorId;
    private String name;
    private Date authorDob;

    public Author(Integer authorId, String name, Date authorDob) {
        this.authorId = authorId;
        this.name = name;
        this.authorDob = authorDob;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
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