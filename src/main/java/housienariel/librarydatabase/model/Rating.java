package housienariel.librarydatabase.model;

public class Rating {

    private Integer ratingId;
    private int ratingValue;

    public Rating(Integer ratingId, int ratingValue) {
        this.ratingId = ratingId;
        this.ratingValue = ratingValue;
    }

    public Integer getRatingId() {
        return ratingId;
    }

    public void setRatingId(Integer ratingId) {
        this.ratingId = ratingId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }
}
