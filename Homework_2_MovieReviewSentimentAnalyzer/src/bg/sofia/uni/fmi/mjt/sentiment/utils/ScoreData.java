package bg.sofia.uni.fmi.mjt.sentiment.utils;

import java.util.Objects;

public class ScoreData {

    private double averageScore = 0.0;
    private int occurrences = 0;

    public ScoreData(int score) {
        averageScore += score;
        occurrences = 1;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public double getScore() {
        return averageScore;
    }

    public void updateScore(int score) {
        averageScore = (averageScore * occurrences + score) / (++occurrences);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreData scoreData = (ScoreData) o;
        return Double.compare(scoreData.averageScore, averageScore) == 0 && occurrences == scoreData.occurrences;
    }

    @Override
    public int hashCode() {
        return Objects.hash(averageScore, occurrences);
    }
}
