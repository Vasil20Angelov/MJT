package bg.sofia.uni.fmi.mjt.escaperoom.room;

public record Review(int rating, String reviewText) {

    public Review {
        if (rating < 0 || rating > 10) {
            throw new IllegalArgumentException("The rating must be in the interval [0,10]");
        }

        if (reviewText == null || reviewText.length() > 200) {
            throw new IllegalArgumentException("Text is either null or too long");
        }
    }
}
