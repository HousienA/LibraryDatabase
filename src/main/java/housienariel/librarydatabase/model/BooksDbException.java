package housienariel.librarydatabase.model;

import org.springframework.dao.DataAccessException;

public class BooksDbException extends DataAccessException {
    public BooksDbException(String message) {
        super(message);
    }

    public BooksDbException(String message, Throwable cause) {
        super(message, cause);
    }
}
