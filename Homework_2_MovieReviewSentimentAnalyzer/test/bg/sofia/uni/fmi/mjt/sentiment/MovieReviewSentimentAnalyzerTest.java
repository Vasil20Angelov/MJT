package bg.sofia.uni.fmi.mjt.sentiment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class MovieReviewSentimentAnalyzerTest {

    public static StringReader getSampleStopwordsReader() {
        String content = """
               i
               me
               a
               and
               is
               it
               it's
               been
               the
               again
               below
               has
               hasn't
               have
               haven't
               than
               of
               """;

        return new StringReader(content);
    }

    public static StringReader getSampleReviewsReader() {
        String content = """
                4 One of the best movie ever.
                2 It could have been better
                3 A nice moViE.  Much better than others!
                1 Awful movie. I have watched better movies.
                2 Below average
                0 The worst movie ever. I will never watch it again!
                4 Amazing plot. It made me watch it again and again, great, exciting and spectacular.
                """;

        return new StringReader(content);
    }

    private static MovieReviewSentimentAnalyzer reviewsAnalyzer;

    @BeforeAll
    public static void initRepository() {
        try (StringReader reviewsReader = getSampleReviewsReader();
            StringReader stopwordsReader = getSampleStopwordsReader()) {
            reviewsAnalyzer = new MovieReviewSentimentAnalyzer(stopwordsReader, reviewsReader, null);
        }
    }

    @Test
    public void testReviewWordsMapCreation() {
        assertEquals(23, reviewsAnalyzer.getSentimentDictionarySize(),
                "Unexpected number of words stored in the repository!");
    }

    @Test
    public void testGetWordSentimentWithNullParameter() {
        assertEquals(-1, reviewsAnalyzer.getWordSentiment(null),
                "Expected result is -1 when the word is null!");
    }

    @Test
    public void testGetWordSentimentReturnsTheCorrectScoreWhenTheWordExistsInTheRepository() {
        assertEquals(2, reviewsAnalyzer.getWordSentiment("movie"),
                "The score is not correctly calculated!");
    }

    @Test
    public void testGetWordSentimentReturnsMinusOneWhenTheWordDoesNotExistInTheRepository() {
        assertEquals(-1, reviewsAnalyzer.getWordSentiment("NonExistingWord"),
                "The returned value for non existing word in the repository must be -1!");
    }

    @Test
    public void testGetWordFrequencyWithNullParameter() {
        assertEquals(-1, reviewsAnalyzer.getWordFrequency(null),
                "Expected result is -1 when the word is null!");
    }

    @Test
    public void testGetWordFrequencyReturnsTheCorrectNumberWhenTheWordExistsInTheRepository() {
        assertEquals(4, reviewsAnalyzer.getWordFrequency("moVie"),
                "Unexpected number of word occurrences in the repository!");
    }

    @Test
    public void testGetWordFrequencyReturnsZeroWhenTheWordDoesNotExistInTheRepository() {
        assertEquals(0, reviewsAnalyzer.getWordFrequency("NonExistingWord"),
                "The returned value for non existing word in the repository must be 0!");
    }

    @Test
    public void testGetMostFrequentWordsThrowsWhenTheGivenArgumentIsNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () -> reviewsAnalyzer.getMostFrequentWords(-1),
                "Expected IllegalArgumentException to be thrown when n is negative!");
    }

    @Test
    public void testGetMostFrequentWordsReturnsCorrectResult() {
        List<String> expected = List.of("movie", "better", "ever");
        assertIterableEquals(expected, reviewsAnalyzer.getMostFrequentWords(3),
                "Unexpected returned order!");
    }

    @Test
    public void testGetMostPositiveWordsThrowsWhenTheGivenArgumentIsNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () -> reviewsAnalyzer.getMostPositiveWords(-1),
                "Expected IllegalArgumentException to be thrown when n is negative!");
    }

    @Test
    public void testGetMostPositiveWordsReturnsCorrectResult() {
        List<String> expected =
                List.of("amazing", "plot", "made", "great", "exciting", "spectacular", "best", "one", "nice");

        List<String> actual = reviewsAnalyzer.getMostPositiveWords(9);

        assertTrue(expected.containsAll(actual), "Unexpected returned words!");

        assertEquals("nice", actual.get(8), "Unexpected returned order");
    }

    @Test
    public void testGetMostNegativeWordsThrowsWhenTheGivenArgumentIsNegativeNumber() {
        assertThrows(IllegalArgumentException.class, () -> reviewsAnalyzer.getMostNegativeWords(-1),
                "Expected IllegalArgumentException to be thrown when n is negative!");
    }

    @Test
    public void testGetMostNegativeWordsReturnsCorrectResult() {
        List<String> expected = List.of("worst", "never", "will", "awful", "watched", "movies", "average");

        List<String> actual = reviewsAnalyzer.getMostNegativeWords(7);

        assertTrue(expected.containsAll(actual), "Unexpected returned words!");

        assertEquals("average", actual.get(6), "Unexpected returned order");
    }

    @Test
    public void testGetReviewSentimentWithNullArgument() {
        assertEquals(-1, reviewsAnalyzer.getReviewSentiment(null),
                "Expected result -1 when the argument is null");
    }

    @Test
    public void testGetReviewSentimentWithTextContainingOnlyStopwords() {
        String review = "The and is it!";
        assertEquals(-1, reviewsAnalyzer.getReviewSentiment(review),
                "Expected result -1 when the review contains only stopwords");
    }

    @Test
    public void testGetReviewSentimentReturnsCorrectSentimentScore() {
        String review = "a nice moVie!";
        assertEquals(2.5, reviewsAnalyzer.getReviewSentiment(review),
                "Unexpected sentiment score!");
    }

    @Test
    public void testGetReviewSentimentAsNameWithNullArgument() {
        assertEquals("unknown", reviewsAnalyzer.getReviewSentimentAsName(null),
                "Expected result \"unknown\" when the review is null");
    }

    @Test
    public void testGetReviewSentimentAsNameReturnsCorrectName() {
        String review = "nice, than";
        assertEquals("somewhat positive", reviewsAnalyzer.getReviewSentimentAsName(review),
                "Unexpected sentiment score as name!");
    }

    @Test
    public void testIsStopWordWithStopword() {
        assertTrue(reviewsAnalyzer.isStopWord("iS"),
                "The method should return true when the given word is a stopword");
    }

    @Test
    public void testIsStopWordWithNonStopword() {
        assertFalse(reviewsAnalyzer.isStopWord("Movie"),
                "The method should return false when the given word is not a stopword");
    }

    @Test
    public void testAppendReviewWithNullReview() {
        assertThrows(IllegalArgumentException.class, () -> reviewsAnalyzer.appendReview(null, 1),
                "Expected IllegalArgumentException when the review is null!");
    }

    @Test
    public void testAppendReviewWithBlankReview() {
        assertThrows(IllegalArgumentException.class, () -> reviewsAnalyzer.appendReview(" ", 1),
                "Expected IllegalArgumentException when the review is blank or empty!");
    }

    @Test
    public void testAppendReviewWithGivenSentimentBelowTheRequiredValues() {
        assertThrows(IllegalArgumentException.class, () -> reviewsAnalyzer.appendReview("nice", -1),
                "Expected IllegalArgumentException when the sentiment is less than the defined lower bound!");
    }

    @Test
    public void testAppendReviewWithGivenSentimentOverTheRequiredValues() {
        assertThrows(IllegalArgumentException.class, () -> reviewsAnalyzer.appendReview("nice", 5),
                "Expected IllegalArgumentException when the sentiment is more than the defined upper bound!");
    }

    @Test
    public void testAppendReviewCorrectlyWritesToTheStream() {
        String reviewsWritten = """
                3 first review.
                4 snd rev
                """;

        int sentimentValue = 3;
        String review = " amaZing movie";
        String expectedStreamOutput = reviewsWritten + sentimentValue + review + System.lineSeparator();
        String actualStreamOutput = null;

        try (StringReader reviewsReader = getSampleReviewsReader();
             StringReader stopwordsReader = getSampleStopwordsReader();
             StringWriter reviewsWriter = new StringWriter()) {

            reviewsWriter.write(reviewsWritten);
            var reviewsAnalyzer = new MovieReviewSentimentAnalyzer(stopwordsReader, reviewsReader, reviewsWriter);

            assertTrue(reviewsAnalyzer.appendReview(review, sentimentValue),
                    "The operation should be successful!");

            actualStreamOutput = reviewsWriter.toString();

        } catch (IOException e) {
            fail(e.getMessage());
        }

        assertEquals(expectedStreamOutput, actualStreamOutput,
                "The review was not correctly written in the stream!");
    }

    @Test
    public void testAppendReviewCorrectlyUpdatesReviewWordsAndScore() {

        int sentimentValue = 4;
        String review = "a nice and fascinating moviE ";

        try (StringReader reviewsReader = getSampleReviewsReader();
             StringReader stopwordsReader = getSampleStopwordsReader();
             StringWriter reviewsWriter = new StringWriter()) {

            var reviewsAnalyzer = new MovieReviewSentimentAnalyzer(stopwordsReader, reviewsReader, reviewsWriter);

            assertTrue(reviewsAnalyzer.appendReview(review, sentimentValue),
                    "The operation should be successful!");

            assertEquals(2.4, reviewsAnalyzer.getWordSentiment("movie"),
                    "Wrongly updated sentiment score for \"movie\"!");
            assertEquals(3.5, reviewsAnalyzer.getWordSentiment("nice"),
                    "Wrongly updated sentiment score for \"nice\"!");
            assertEquals(4, reviewsAnalyzer.getWordSentiment("fascinating"),
                    "Wrongly updated sentiment score for \"fascinating\"!");

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
