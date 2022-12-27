package bg.sofia.uni.fmi.mjt.sentiment.reviewData;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReviewWordsRepositoryTest {

    public static Set<String> getSampleStopwords() {
        return new HashSet<>() {
            {
                add("i");
                add("me");
                add("a");
                add("and");
                add("it");
                add("it's");
                add("been");
                add("the");
                add("again");
                add("below");
                add("has");
                add("hasn't");
                add("have");
                add("haven't");
                add("than");
                add("of");
            }
        };
    }

    public static StringReader getSampleReader() {
        String content = """
                5 One of the best movie ever.
                3 It could have been better
                4 A nice moViE.  Much better than others!
                1 Awful movie. I have watched better movies.
                2 Below average
                0 The worst movie ever. I will never watch it again!
                5 Amazing plot. It made me watch it again and again, great, exciting and spectacular.
                """;

        return new StringReader(content);
    }

    private static ReviewWordsRepository reviewWordsRepository;

    @BeforeAll
    public static void initRepository() {
        try (StringReader reader = getSampleReader()) {
            reviewWordsRepository = new ReviewWordsRepository(reader, getSampleStopwords());
        }
    }

    @Test
    public void testReviewWordsRepositoryCreation() {
        assertEquals(23, reviewWordsRepository.getSentimentDictionarySize(),
                "Unexpected number of words stored in the repository!");
    }

    @Test
    public void testGetWordSentimentReturnsTheCorrectScoreWhenTheWordExistsInTheRepository() {
        assertEquals(2.5, reviewWordsRepository.getWordSentiment("movie"),
                "The score is not correctly calculated!");
    }

    @Test
    public void testGetWordSentimentReturnsMinusOneWhenTheWordDoesNotExistInTheRepository() {
        assertEquals(-1, reviewWordsRepository.getWordSentiment("NonExistingWord"),
                "The returned value for non existing word in the repository must be -1!");
    }

    @Test
    public void testGetWordFrequencyReturnsTheCorrectNumberWhenTheWordExistsInTheRepository() {
        assertEquals(4, reviewWordsRepository.getWordFrequency("moVie"),
                "Unexpected number of word occurrences in the repository!");
    }

    @Test
    public void testGetWordFrequencyReturnsZeroWhenTheWordDoesNotExistInTheRepository() {
        assertEquals(0, reviewWordsRepository.getWordFrequency("NonExistingWord"),
                "The returned value for non existing word in the repository must be 0!");
    }

    @Test
    public void testGetMostFrequentWordsThrowsWhenTheGivenArgumentIsNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () -> reviewWordsRepository.getMostFrequentWords(-1),
                "Expected IllegalArgumentException to be thrown when n is negative!");
    }

    @Test
    public void testGetMostFrequentWordsReturnsCorrectResult() {
        List<String> expected = new ArrayList<>() {
            {
                add("movie");
                add("better");
                add("ever");
            }
        };

        assertIterableEquals(expected, reviewWordsRepository.getMostFrequentWords(3),
                "Unexpected returned order!");
    }

    @Test
    public void testGetMostPositiveWordsThrowsWhenTheGivenArgumentIsNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () -> reviewWordsRepository.getMostPositiveWords(-1),
                "Expected IllegalArgumentException to be thrown when n is negative!");
    }

    @Test
    public void testGetMostPositiveWordsReturnsCorrectResult() {
        List<String> expected = new ArrayList<>() {
            {
                add("amazing");
                add("plot");
                add("made");
                add("great");
                add("exciting");
                add("spectacular");
                add("best");
                add("one");
                add("nice");
            }
        };

        List<String> actual = reviewWordsRepository.getMostPositiveWords(9);

        assertTrue(expected.containsAll(actual), "Unexpected returned words!");

        assertEquals("nice", actual.get(8), "Unexpected returned order");
    }

    @Test
    public void testGetMostNegativeWordsThrowsWhenTheGivenArgumentIsNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () -> reviewWordsRepository.getMostNegativeWords(-1),
                "Expected IllegalArgumentException to be thrown when n is negative!");
    }

    @Test
    public void testGetMostNegativeWordsReturnsCorrectResult() {
        List<String> expected = new ArrayList<>() {
            {
                add("worst");
                add("never");
                add("will");
                add("awful");
                add("watched");
                add("movies");
                add("average");
            }
        };

        List<String> actual = reviewWordsRepository.getMostNegativeWords(7);

        assertTrue(expected.containsAll(actual), "Unexpected returned words!");

        assertEquals("average", actual.get(6), "Unexpected returned order");
    }


}
