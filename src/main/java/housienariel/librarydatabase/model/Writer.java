package housienariel.librarydatabase.model;

public class Writer {
    private String bookISBN;
    private int authorId;

    public Writer(String bookISBN, int authorId) {
        this.bookISBN = bookISBN;
        this.authorId = authorId;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
}