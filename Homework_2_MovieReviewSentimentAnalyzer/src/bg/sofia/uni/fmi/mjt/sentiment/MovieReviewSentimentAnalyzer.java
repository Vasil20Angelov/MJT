package bg.sofia.uni.fmi.mjt.sentiment;

import bg.sofia.uni.fmi.mjt.sentiment.reviewData.ReviewWordsRepository;
import bg.sofia.uni.fmi.mjt.sentiment.reviewData.StopwordsRepository;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MovieReviewSentimentAnalyzer implements SentimentAnalyzer {

    private static final String WORD_SEPARATOR_REGEX = "[\\p{IsPunctuation}\\s]+";

    private static final List<String> SCORE_NAMES  = new ArrayList<>() {
        {
            add("negative");
            add("somewhat negative");
            add("neutral");
            add("somewhat positive");
            add("positive");
        }
    };

    private final Set<String> stopwords;
    private final ReviewWordsRepository reviewWordsRepository;
    private final Writer reviewsOut;

    public MovieReviewSentimentAnalyzer(Reader stopwordsIn, Reader reviewsIn, Writer reviewsOut) {
        this.reviewsOut = reviewsOut;
        stopwords = StopwordsRepository.getStopwords(stopwordsIn);
        reviewWordsRepository = new ReviewWordsRepository(reviewsIn, stopwords);
    }

    @Override
    public double getReviewSentiment(String review) {
        if (review == null) {
            return -1;
        }

        String[] words = review.split(WORD_SEPARATOR_REGEX);
        double score = 0.0;
        int nonStopwords = 0;
        for (String word : words) {
            if (!stopwords.contains(word)) {
                score += reviewWordsRepository.getWordSentiment(word);
                nonStopwords++;
            }
        }

        return nonStopwords != 0 ? score / nonStopwords : 0.0;
    }

    @Override
    public String getReviewSentimentAsName(String review) {
        int roundedValue = (int) Math.round(getReviewSentiment(review));
        if (roundedValue == -1) {
            return "unknown";
        }
        return SCORE_NAMES.get(roundedValue);
    }

    @Override
    public double getWordSentiment(String word) {
        if (word == null) {
            return -1;
        }

        return reviewWordsRepository.getWordSentiment(word);
    }

    @Override
    public int getWordFrequency(String word) {
        if (word == null) {
            return -1;
        }

        return reviewWordsRepository.getWordFrequency(word);
    }

    @Override
    public List<String> getMostFrequentWords(int n) {
        return reviewWordsRepository.getMostFrequentWords(n);
    }

    @Override
    public List<String> getMostPositiveWords(int n) {
        return reviewWordsRepository.getMostPositiveWords(n);
    }

    @Override
    public List<String> getMostNegativeWords(int n) {
        return reviewWordsRepository.getMostNegativeWords(n);
    }

    @Override
    public boolean appendReview(String review, int sentiment) {
        return false;
    }

    @Override
    public int getSentimentDictionarySize() {
        return reviewWordsRepository.getSentimentDictionarySize();
    }

    @Override
    public boolean isStopWord(String word) {
        return stopwords.contains(word);
    }
}
