package housienariel.librarydatabase.model;

public class Rating {
    private Integer ratingId;
    private String bookISBN;
    private int ratingValue;

    public Rating(Integer ratingId, String bookISBN, int ratingValue) {
        this.ratingId = ratingId;
        this.bookISBN = bookISBN;
        this.ratingValue = ratingValue;
    }

    public Integer getRatingId() {
        return ratingId;
    }

    public void setRatingId(Integer ratingId) {
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

