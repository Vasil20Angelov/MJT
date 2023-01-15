package bg.sofia.uni.fmi.mjt.sentiment.utils;

import java.util.Objects;

public class ScoreData {

    private double averageScore = 0.0;
    private int uniqueOccurrencesInReview = 1;
    private int totalOccurrences = 1;

    public ScoreData(int score) {
        averageScore += score;
    }

    public int getTotalOccurrences() {
        return totalOccurrences;
    }

    public double getScore() {
        return averageScore;
    }

    public void addOccurrence() {
        totalOccurrences++;
    }

    public void updateScore(int score) {
        averageScore = (averageScore * uniqueOccurrencesInReview + score) / (++uniqueOccurrencesInReview);
        addOccurrence();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreData scoreData = (ScoreData) o;
        return Double.compare(scoreData.averageScore, averageScore) == 0
                && uniqueOccurrencesInReview == scoreData.uniqueOccurrencesInReview
                && totalOccurrences == scoreData.totalOccurrences;
    }

    @Override
    public int hashCode() {
        return Objects.hash(averageScore, uniqueOccurrencesInReview, totalOccurrences);
    }
}
