package bg.sofia.uni.fmi.mjt.sentiment.utils.factories;

import bg.sofia.uni.fmi.mjt.sentiment.utils.ScoreData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ReviewWordsFactory {

    public static final String WORD_SEPARATOR_REGEX = "[\\p{IsPunctuation}\\s]+";
    public static final String WORD_REGEX = "[0-9a-zA-Z']{2,}";

    public static Map<String, ScoreData> create(Reader reader, Set<String> stopwords) {
        Map<String, ScoreData> words = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> lineContent = List.of(line.split(WORD_SEPARATOR_REGEX));
                int sentimentScore = Integer.parseInt(lineContent.get(0).strip());

                updateCollection(words, stopwords, lineContent.subList(1, lineContent.size()), sentimentScore);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading the file!", e);
        }

        return words;
    }

    public static void updateCollection(Map<String, ScoreData> collection, Set<String> stopwords,
                                        List<String> reviewContent, int sentiment) {

        Set<String> uniqueWordsInReview = new HashSet<>();
        for (String word : reviewContent) {

            String lowerCaseWord = word.strip().toLowerCase();
            if (Pattern.matches(WORD_REGEX, lowerCaseWord) && !stopwords.contains(lowerCaseWord)
                    && !uniqueWordsInReview.contains(lowerCaseWord)) {

                ScoreData scoreData = collection.get(lowerCaseWord);
                if (scoreData == null) {
                    collection.put(lowerCaseWord, new ScoreData(sentiment));
                } else {
                    scoreData.updateScore(sentiment);
                }

                uniqueWordsInReview.add(lowerCaseWord);
            }
        }
    }
}
