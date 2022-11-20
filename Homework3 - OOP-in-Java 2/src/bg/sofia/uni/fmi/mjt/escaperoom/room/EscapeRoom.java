package bg.sofia.uni.fmi.mjt.escaperoom.room;

import bg.sofia.uni.fmi.mjt.escaperoom.rating.Ratable;

import java.util.Arrays;
import java.util.Objects;

public class EscapeRoom implements Ratable {
    private final String name;
    private final Theme theme;
    private final Difficulty difficulty;
    private final int maxTimeToEscape;
    private final double priceToPlay;
    private final int maxReviewsCount;
    private Review[] reviews;
    private int totalRating;
    private int totalReviews;

    public EscapeRoom(String name, Theme theme, Difficulty difficulty, int maxTimeToEscape, double priceToPlay, int maxReviewsCount) {
        this.name = name;
        this.theme = theme;
        this.difficulty = difficulty;
        this.maxTimeToEscape = maxTimeToEscape;
        this.priceToPlay = priceToPlay;
        this.maxReviewsCount = maxReviewsCount;
        totalRating = 0;
        totalReviews = 0;
        reviews = new Review[0];
    }

    /**
     * Returns the name of the escape room.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the difficulty of the escape room.
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the maximum time to escape the room.
     */
    public int getMaxTimeToEscape() {
        return maxTimeToEscape;
    }

    /**
     * Returns all user reviews stored for this escape room, in the order they have been added.
     */
    public Review[] getReviews() {
        return reviews;
    }

    /**
     * Adds a user review for this escape room.
     *
     * @param review the user review to add.
     */
    public void addReview(Review review) {
        if (reviews.length == maxReviewsCount && maxReviewsCount > 0) {
            for (int i = 0; i < reviews.length - 1; i++) {
                reviews[i] = reviews[i + 1];
            }
            reviews[reviews.length - 1] = review;
        } else if (reviews.length < maxReviewsCount) {
            Review[] temp = reviews;
            reviews = new Review[reviews.length + 1];
            System.arraycopy(temp, 0, reviews, 0, temp.length);

            reviews[temp.length] = review;
        }

        totalRating += review.rating();
        totalReviews++;
    }

    @Override
    public double getRating() {
        if (reviews.length == 0) {
            return 0.0;
        }

        return (double) totalRating / totalReviews;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EscapeRoom that = (EscapeRoom) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, theme, difficulty, maxTimeToEscape, totalRating, priceToPlay, maxReviewsCount, totalReviews, Arrays.hashCode(reviews));
        result = 31 * result;
        return result;
    }
}
