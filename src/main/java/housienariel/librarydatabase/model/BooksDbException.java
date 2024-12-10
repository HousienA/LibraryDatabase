package housienariel.librarydatabase.model;

import java.sql.SQLException;

public class BooksDbException extends SQLException {
    public BooksDbException(String message) {
        super(message);
    }

    public BooksDbException(String message, Throwable cause) {
        super(message, cause);
    }
}
