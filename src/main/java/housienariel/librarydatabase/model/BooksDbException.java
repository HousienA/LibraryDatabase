package housienariel.librarydatabase.model;

public class BooksDbException extends Exception {
    public BooksDbException(String message) {
        super(message);
    }

    public BooksDbException(String message, Throwable cause) {
        super(message, cause);
    }
}
