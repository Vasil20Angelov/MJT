package bg.sofia.uni.fmi.mjt.sentiment.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ScoreDataTest {
    
    @Test
    public void testUpdateScore() {
        ScoreData scoreData = new ScoreData(4);
        scoreData.addOccurrence();
        scoreData.updateScore(1);

        assertEquals(2.5, scoreData.getScore(), "Unexpected score!");
        assertEquals(3, scoreData.getTotalOccurrences(), "Unexpected count!");
    }
    
    @Test
    public void testEqualityAfterUpdatingScore() {
        ScoreData scoreData1 = new ScoreData(2);
        scoreData1.updateScore(2);

        ScoreData scoreData2 = new ScoreData(4);
        scoreData2.updateScore(0);

        assertEquals(scoreData1, scoreData2,
                "The objects should be equal as they have the same score and occurrences!");
        assertEquals(scoreData1.hashCode(), scoreData2.hashCode(),
                "The objects should have the same hash code when they are equal!");
    }

}
