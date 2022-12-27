package bg.sofia.uni.fmi.mjt.sentiment.reviewData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class StopwordsRepository {

    public static Set<String> getStopwords(Reader reader) {
        Set<String> stopwords = new HashSet<>();

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String word;
            while ((word = bufferedReader.readLine()) != null) {
                stopwords.add(word.toLowerCase());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while reading the file!", e);
        }

        return stopwords;
    }

}
