package bg.sofia.uni.fmi.mjt.netflix;

import java.util.Arrays;
import java.util.List;

public record Content(String id, String title, ContentType type, String description, int releaseYear, int runtime,
                       List<String> genres, int seasons, String imdbId, double imdbScore, double imdbVotes) {

    private static final String CONTENT_ATTRIBUTE_DELIMITER = ",";
    private static final int EXPECTED_TOKENS = 11;

    public static Content of(String line) {
        List<String> tokens = Arrays.stream(line.split(CONTENT_ATTRIBUTE_DELIMITER))
                .map(x -> x.strip())
                .toList();

        if (tokens.size() != EXPECTED_TOKENS) {
            return null;
        }

        String genres = tokens.get(Token.GENRES.ordinal());
        int leftBracket = genres.indexOf('[');
        int rightBracket = genres.indexOf(']');
        List<String> genresList = Arrays.stream(genres.substring(leftBracket + 1, rightBracket)
                .split(";"))
                .map(x -> x.strip())
                .map(x -> x.replaceAll("\'", ""))
                .map(x -> x.toLowerCase())
                .toList();

        String id = tokens.get(Token.ID.ordinal());
        String title = tokens.get(Token.TITLE.ordinal());
        String description = tokens.get(Token.DESCRIPTION.ordinal());
        String imdbId = tokens.get(Token.IMDB_ID.ordinal());
        ContentType type = ContentType.valueOf(tokens.get(Token.TYPE.ordinal()));
        int releaseYear = Integer.parseInt(tokens.get(Token.RELEASE_YEAR.ordinal()));
        int runTime = Integer.parseInt(tokens.get(Token.RUNTIME.ordinal()));
        int seasons = Integer.parseInt(tokens.get(Token.SEASONS.ordinal()));
        double imdbScore = Double.parseDouble(tokens.get(Token.IMDB_SCORE.ordinal()));
        double imdbVotes = Double.parseDouble(tokens.get(Token.IMDB_VOTES.ordinal()));

        return new Content(id, title, type, description, releaseYear, runTime,
                            genresList, seasons, imdbId, imdbScore, imdbVotes);
    }

    public double getWeightedRating() {
        double votes = imdbVotes();
        double score = imdbScore();
        double avr = votes != 0 ? score / votes : 0;
        final double constant = 10_000;

        return (votes / (votes + constant)) * avr + (constant / (votes + constant)) * score;
    }

    public int similarity(Content content) {
        return (int) genres.stream()
                        .filter(content.genres::contains)
                        .count();
    }
}
