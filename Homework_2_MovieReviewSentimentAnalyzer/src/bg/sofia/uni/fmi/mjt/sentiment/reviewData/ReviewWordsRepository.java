package bg.sofia.uni.fmi.mjt.sentiment.reviewData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReviewWordsRepository {

    private static final String WORD_SEPARATOR_REGEX = "[\\p{IsPunctuation}\\s]+";
    private static final String WORD_REGEX = "[0-9a-zA-Z']{2,}";


    private final Map<String, ScoreData> words = new HashMap<>();
    private final Set<String> stopwordsRepository;

    public ReviewWordsRepository(Reader reader, Set<String> stopwordsRepository) {
        this.stopwordsRepository = stopwordsRepository;
        fillWordsData(reader);
    }

    private void fillWordsData(Reader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

            String line;
            while ((line = bufferedReader.readLine()) != null) {

                String[] lineContent = line.split(WORD_SEPARATOR_REGEX);
                int score = Integer.parseInt(lineContent[0].trim());
                for (int i = 1; i < lineContent.length; i++) {

                    String lowerCaseWord = lineContent[i].trim().toLowerCase();
                    if (Pattern.matches(WORD_REGEX, lowerCaseWord) && !stopwordsRepository.contains(lowerCaseWord)) {
                        ScoreData scoreData = words.get(lowerCaseWord);
                        if (scoreData == null) {
                            words.put(lowerCaseWord, new ScoreData(score));
                        }
                        else {
                            scoreData.updateScore(score);
                        }
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading the file!", e);
        }
    }

    public double getWordSentiment(String word) {
        ScoreData scoreData = words.get(word);
        return scoreData == null ? -1 : scoreData.getScore();
    }

    public int getSentimentDictionarySize() {
        return words.keySet().size();
    }

    public int getWordFrequency(String word) {
        word = word.toLowerCase();
        if (!words.containsKey(word)) {
            return 0;
        }

        return words.get(word).getOccurrences();
    }

    public List<String> getMostFrequentWords(int n) {
        assertParameterIsNotNegative(n);

        return words.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry ->
                        ((Map.Entry<String, ScoreData>)entry).getValue().getOccurrences()).reversed())
                .map(Map.Entry::getKey)
                .limit(n)
                .collect(Collectors.toList());
    }

    public List<String> getMostPositiveWords(int n) {
        assertParameterIsNotNegative(n);

        return words.entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(entry ->
                        ((Map.Entry<String, ScoreData>)entry).getValue().getScore()).reversed())
                .map(Map.Entry::getKey)
                .limit(n)
                .collect(Collectors.toList());
    }

    public List<String> getMostNegativeWords(int n) {
        assertParameterIsNotNegative(n);

        return words.entrySet()
                .stream()
                .sorted(Comparator.comparingDouble(entry -> entry.getValue().getScore()))
                .map(Map.Entry::getKey)
                .limit(n)
                .collect(Collectors.toList());
    }

    private void assertParameterIsNotNegative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid argument!");
        }
    }
}
