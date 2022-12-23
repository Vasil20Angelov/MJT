package bg.sofia.uni.fmi.mjt.netflix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetflixRecommender {

    private final static Pattern WORD_SEPARATOR_PATTERN = Pattern.compile("[\\p{IsPunctuation}\\s]+");
    private List<Content> contents;

    /**
     * Loads the dataset from the given {@code reader}.
     *
     * @param reader Reader from which the dataset can be read.
     */
    public NetflixRecommender(Reader reader) {
        try (var bufferedReader = new BufferedReader(reader)) {
            contents = bufferedReader.lines()
                    .skip(1)
                    .map(Content::of)
                    .filter(Objects::nonNull)
                    .toList();
        }
        catch (IOException ex) {
            throw new IllegalStateException("Reading error!");
        }
    }

    /**
     * Returns all movies and shows from the dataset in undefined order as an unmodifiable List.
     * If the dataset is empty, returns an empty List.
     *
     * @return the list of all movies and shows.
     */
    public List<Content> getAllContent() {
        return Collections.unmodifiableList(contents);
    }

    /**
     * Returns a list of all unique genres of movies and shows in the dataset in undefined order.
     * If the dataset is empty, returns an empty List.
     *
     * @return the list of all genres
     */
    public List<String> getAllGenres() {
        return contents.stream()
                .flatMap(x -> Stream.of(x.genres()))
                .flatMap(List::stream)
                .distinct()
                .filter(s -> !s.isBlank())
                .toList();
    }

    /**
     * Returns the movie with the longest duration / run time. If there are two or more movies
     * with equal maximum run time, returns any of them. Shows in the dataset are not considered by this method.
     *
     * @return the movie with the longest run time
     * @throws NoSuchElementException in case there are no movies in the dataset.
     */
    public Content getTheLongestMovie() {
        return contents.stream()
                .filter(x -> x.type() == ContentType.MOVIE)
                .max(Comparator.comparing(Content::runtime))
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns a breakdown of content by type (movie or show).
     *
     * @return a Map with key: a ContentType and value: the set of movies or shows on the dataset, in undefined order.
     */
    public Map<ContentType, Set<Content>> groupContentByType() {
        return contents.stream()
                .collect(Collectors.groupingBy(Content::type, Collectors.toSet()));
    }

    /**
     * Returns the top N movies and shows sorted by weighed IMDB rating in descending order.
     * If there are fewer movies and shows than {@code n} in the dataset, return all of them.
     * If {@code n} is zero, returns an empty list.
     *
     * The weighed rating is calculated by the following formula:
     * Weighted Rating (WR) = (v ÷ (v + m)) × R + (m ÷ (v + m)) × C
     * where
     * R is the content's own average rating across all votes. If it has no votes, its R is 0.
     * C is the average rating of content across the dataset
     * v is the number of votes for a content
     * m is a tunable parameter: sensitivity threshold. In our algorithm, it's a constant equal to 10_000.
     *
     * Check https://stackoverflow.com/questions/1411199/what-is-a-better-way-to-sort-by-a-5-star-rating for details.
     *
     * @param n the number of the top-rated movies and shows to return
     * @return the list of the top-rated movies and shows
     * @throws IllegalArgumentException if {@code n} is negative.
     */
    public List<Content> getTopNRatedContent(int n) {

        if (n < 0) {
            throw new IllegalArgumentException("n must not be negative!");
        }

        return contents.stream()
                .sorted(Comparator.comparing(Content::getWeightedRating).reversed())
                .limit(n)
                .toList();
    }

    /**
     * Returns a list of content similar to the specified one sorted by similarity in descending order.
     * Two contents are considered similar, only if they are of the same type (movie or show).
     * The used measure of similarity is the number of genres two contents share.
     * If two contents have equal number of common genres with the specified one, their mutual order
     * in the result is undefined.
     *
     * @param content the specified movie or show.
     * @return the sorted list of content similar to the specified one.
     */
    public List<Content> getSimilarContent(Content content) {
        return contents.stream()
                .filter(x -> x.type().equals(content.type()))
                .sorted(Comparator.comparing(content::similarity).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Searches content by keywords in the description (case-insensitive).
     *
     * @param keywords the keywords to search for
     * @return an unmodifiable set of movies and shows whose description contains all specified keywords.
     */
    public Set<Content> getContentByKeywords(String... keywords) {
        List<String> keywordsList = Arrays.stream(keywords).map(String::toLowerCase).toList();

        return contents.stream()
                .filter(x -> WORD_SEPARATOR_PATTERN.splitAsStream(x.description())
                        .map(word -> word.toLowerCase())
                        .toList()
                        .containsAll(keywordsList))
                .collect(Collectors.toUnmodifiableSet());
    }

}