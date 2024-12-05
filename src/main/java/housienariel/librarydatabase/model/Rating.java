package housienariel.librarydatabase.model;

public class Rating {
    private int ratingId;
    private String bookISBN;
    private int ratingValue;

    public Rating(int ratingId, String bookISBN, int ratingValue) {
        this.ratingId = ratingId;
        this.bookISBN = bookISBN;
        this.ratingValue = ratingValue;
    }

    public int getRatingId() {
        return ratingId;
    }

    public void setRatingId(int ratingId) {
        this.ratingId = ratingId;
    }

    public String getBookISBN() {
        return bookISBN;
    }

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }
}

