package bg.sofia.uni.fmi.mjt.sentiment.utils.factories;

import bg.sofia.uni.fmi.mjt.sentiment.utils.ScoreData;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReviewWordsFactoryTest {

    public static Set<String> getSampleStopwords() {
        return new HashSet<>() {
            {
                add("me");
                add("a");
                add("it's");
                add("been");
                add("the");
                add("below");
                add("have");
                add("haven't");
                add("of");
            }
        };
    }

    @Test
    public void testCreateWithoutUpdatingApplied() {
        String content = """
                4 One of the best movies
                3 New movie
                """;

        Map<String, ScoreData> actual;
        try (StringReader reader = new StringReader(content)) {
            actual = ReviewWordsFactory.create(reader, getSampleStopwords());
        }

        Map<String, ScoreData> expected = new HashMap<>() {
            {
                put("one", new ScoreData(4));
                put("best", new ScoreData(4));
                put("movies", new ScoreData(4));
                put("new", new ScoreData(3));
                put("movie", new ScoreData(3));
            }
        };

        assertEquals(expected, actual, "Unexpected words or wrongly calculated words score!");
    }

    @Test
    public void testCreateWithUpdatingApplied() {
        String content = """
                4 One of the best movie ever.
                3 A nice moViE.
                2 Below average
                0 The worst movie ever
                """;

        Map<String, ScoreData> actual;
        try (StringReader reader = new StringReader(content)) {
            actual = ReviewWordsFactory.create(reader, getSampleStopwords());
        }

        Map<String, ScoreData> expected = new HashMap<>() {
            {
                put("one", new ScoreData(4));
                put("best", new ScoreData(4));
                put("ever", new ScoreData(4));
                put("movie", new ScoreData(4));
                put("nice", new ScoreData(3));
                put("average", new ScoreData(2));
                put("worst", new ScoreData(0));
            }
        };

        expected.get("ever").updateScore(0);
        expected.get("movie").updateScore(3);
        expected.get("movie").updateScore(0);

        assertEquals(expected, actual, "Unexpected words or wrongly calculated words score!");
    }

    @Test
    public void testCreateWithDataThatContainsSameWordMultipleTimesInOneReview() {
        String content = """
                4 good, awesome,  good
                3 good
                4 awesome, awesome
                1 awesome
                """;

        Map<String, ScoreData> actual;
        try (StringReader reader = new StringReader(content)) {
            actual = ReviewWordsFactory.create(reader, getSampleStopwords());
        }

        assertEquals(3.5, actual.get("good").getScore(),
                "Wrongly calculated words score!");
        assertEquals(3, actual.get("awesome").getScore(),
                "Wrongly calculated words score!");
        assertEquals(2, actual.get("good").getOccurrences(),
                "Wrongly calculated words occurrences!");
        assertEquals(3, actual.get("awesome").getOccurrences(),
                "Wrongly calculated words occurrences!");
    }

    @Test
    public void testUpdateCollection() {
        String content = """
                4 One of the best movies
                3 New movie
                """;

        Map<String, ScoreData> actual;
        try (StringReader reader = new StringReader(content)) {
            actual = ReviewWordsFactory.create(reader, getSampleStopwords());
        }

        List<String> review = List.of("awful", "movie");
        ReviewWordsFactory.updateCollection(actual, getSampleStopwords(), review, 0);

        assertEquals(1.5, actual.get("movie").getScore(),
                "Unexpected sentiment score for \"movie\"!");

        assertEquals(0, actual.get("awful").getScore(),
                "Unexpected sentiment score for \"awful\"!");
    }
}
