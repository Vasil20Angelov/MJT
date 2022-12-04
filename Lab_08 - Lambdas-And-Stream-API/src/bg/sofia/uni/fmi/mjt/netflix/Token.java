package bg.sofia.uni.fmi.mjt.netflix;

public enum Token {
    ID(0),
    TITLE(1),
    TYPE(2),
    DESCRIPTION(3),
    RELEASE_YEAR(4),
    RUNTIME(5),
    GENRES(6),
    SEASONS(7),
    IMDB_ID(8),
    IMDB_SCORE(9),
    IMDB_VOTES(10);

    public final int index;
    Token(int i) {
        index = i;
    }
}
