package bg.sofia.uni.fmi.mjt.sentiment.reviewData;

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
        if (occurrences == 0) {
            averageScore = score;
            occurrences = 1;
        }

        averageScore = (averageScore * occurrences + score) / (++occurrences);
    }
}
