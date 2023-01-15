package bg.sofia.uni.fmi.mjt.sentiment;

import bg.sofia.uni.fmi.mjt.sentiment.utils.factories.ReviewWordsFactory;
import bg.sofia.uni.fmi.mjt.sentiment.utils.ScoreData;
import bg.sofia.uni.fmi.mjt.sentiment.utils.factories.StopwordsFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.sentiment.utils.factories.ReviewWordsFactory.WORD_SEPARATOR_REGEX;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {

    private static final List<String> SCORE_NAMES  = new ArrayList<>() {
        {
            add("negative");
            add("somewhat negative");
            add("neutral");
            add("somewhat positive");
            add("positive");
        }
    };

    private static final int SENTIMENT_LOWER_BOUNDARY = 0;
    private static final int SENTIMENT_UPPER_BOUNDARY = 4;
    private static final int MISSING_SENTIMENT_SCORE = -1;

    private final Set<String> stopwords;
    private final Map<String, ScoreData> reviewWords;
    private final Writer reviewsOut;


    public MovieReviewSentimentAnalyzer(Reader stopwordsIn, Reader reviewsIn, Writer reviewsOut) {
        this.reviewsOut = reviewsOut;
        stopwords = StopwordsFactory.create(stopwordsIn);
        reviewWords = ReviewWordsFactory.create(reviewsIn, stopwords);
    }

    @Override
    public double getReviewSentiment(String review) {
        if (review == null) {
            return MISSING_SENTIMENT_SCORE;
        }

        double score = 0.0;
        int nonStopwords = 0;
        String[] words = review.split(WORD_SEPARATOR_REGEX);

        for (String word : words) {
            String convertedWord = word.strip().toLowerCase();
            if (!stopwords.contains(convertedWord)) {
                score += getWordSentiment(convertedWord);
                nonStopwords++;
            }
        }

        return nonStopwords != 0 ? score / nonStopwords : MISSING_SENTIMENT_SCORE;
    }

    @Override
    public String getReviewSentimentAsName(String review) {
        int roundedValue = (int) Math.round(getReviewSentiment(review));
        if (roundedValue == MISSING_SENTIMENT_SCORE) {
            return "unknown";
        }

        return SCORE_NAMES.get(roundedValue);
    }

    @Override
    public double getWordSentiment(String word) {
        if (word == null) {
            return MISSING_SENTIMENT_SCORE;
        }

        ScoreData scoreData = reviewWords.get(word.strip().toLowerCase());
        return scoreData == null ? MISSING_SENTIMENT_SCORE : scoreData.getScore();
    }

    @Override
    public int getWordFrequency(String word) {
        if (word == null) {
            return -1;
        }

        word = word.strip().toLowerCase();
        if (!reviewWords.containsKey(word)) {
            return 0;
        }

        return reviewWords.get(word).getTotalOccurrences();
    }

    @Override
    public List<String> getMostFrequentWords(int n) {
        assertParameterIsNotNegative(n);

        return reviewWords.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry ->
                        ((Map.Entry<String, ScoreData>)entry).getValue().getTotalOccurrences()).reversed())
                .map(Map.Entry::getKey)
                .limit(n)
                .toList();
    }

    @Override
    public List<String> getMostPositiveWords(int n) {
        assertParameterIsNotNegative(n);

        return reviewWords.entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(entry ->
                        ((Map.Entry<String, ScoreData>)entry).getValue().getScore()).reversed())
                .map(Map.Entry::getKey)
                .limit(n)
                .toList();
    }

    @Override
    public List<String> getMostNegativeWords(int n) {
        assertParameterIsNotNegative(n);

        return reviewWords.entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(entry -> entry.getValue().getScore()))
                .map(Map.Entry::getKey)
                .limit(n)
                .toList();
    }

    @Override
    public boolean appendReview(String review, int sentiment) {
        assertStringIsValid(review);
        assertSentimentValueIsInBounds(sentiment);

        try {
            String lineToWrite = sentiment + " " + review.strip() + System.lineSeparator();
            reviewsOut.append(lineToWrite);
            reviewsOut.flush();
            List<String> reviewContent = List.of(review.split(WORD_SEPARATOR_REGEX));
            ReviewWordsFactory.updateCollection(reviewWords, stopwords, reviewContent, sentiment);
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public int getSentimentDictionarySize() {
        return reviewWords.keySet().size();
    }

    @Override
    public boolean isStopWord(String word) {
        return stopwords.contains(word.toLowerCase().strip());
    }

    private void assertParameterIsNotNegative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid argument!");
        }
    }

    private void assertStringIsValid(String string) {
        if (string == null || string.isBlank()) {
            throw new IllegalArgumentException("Invalid review argument!");
        }
    }

    private void assertSentimentValueIsInBounds(int sentiment) {
        if (sentiment < SENTIMENT_LOWER_BOUNDARY || sentiment > SENTIMENT_UPPER_BOUNDARY) {
            throw new IllegalArgumentException("Invalid sentiment value!");
        }
    }
}
