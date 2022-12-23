package bg.sofia.uni.fmi.mjt.netflix;

import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class NetflixRecommenderTest {

    private static final String testData = """
        id,title,type,description,release_year,runtime,genres,seasons,imdb_id,imdb_score,imdb_votes,
        tm84618,Taxi Driver,MOVIE,Some descr.,1976,114,['drama'; 'crime'],-1,tt0075314,8.2,808582.0
        tm154986,Deliverance,MOVIE,Some descr2.,1972,109,['Drama'; 'action'; 'thriller'; 'european'],-1,t23,7.7,107.0
        tm154987,FakeShow,SHOW,Some descr3.,2000,115,['sci-fi'; 'acTion'; 'thriller'; 'european'],3,t24,6.5,120.0
        tm154988,FakeMovie,MOVIE,Some descr4.,1985,110,['Drama'; 'thriller'; 'action'],-1,t25,7.8,108.0
            """;

    private static final List<Content> expectedContent = new ArrayList<>() {
        {
            add(new Content("tm84618","Taxi Driver", ContentType.MOVIE, "Some descr.",1976,
                    114, List.of("drama", "crime"),
                    -1, "tt0075314", 8.2, 808582.0));

            add(new Content("tm154986","Deliverance", ContentType.MOVIE, "Some descr2.",1972,
                    109, List.of("drama", "action", "thriller", "european"),
                    -1, "t23", 7.7, 107.0));

            add(new Content("tm154987","FakeShow", ContentType.SHOW, "Some descr3.",2000,
                    115, List.of("sci-fi", "action", "thriller", "european"),
                    3, "t24", 6.5, 120.0));

            add(new Content("tm154988","FakeMovie", ContentType.MOVIE, "Some descr4.",1985,
                    110, List.of("drama", "thriller", "action"),
                    -1, "t25", 7.8, 108.0));
        }
    };

    private NetflixRecommender getSample() {
        Reader reader = new StringReader(testData);
        return new NetflixRecommender(reader);
    }

    @Test
    public void testConstructorProperlyLoadsDataFromGivenReader() {
        NetflixRecommender netflixRecommender = getSample();
        List<Content> contentList = netflixRecommender.getAllContent();

        assertIterableEquals(expectedContent, contentList, "The parsing is invalid!");
    }

    @Test
    public void testGetAllGenresReturnsAllDistinctGenres() {
        NetflixRecommender netflixRecommender = getSample();
        List<String> genres = netflixRecommender.getAllGenres();
        List<String> expectedGenres = List.of("drama", "crime", "action", "thriller", "european", "sci-fi");

        assertIterableEquals(expectedGenres, genres, "Actual genres differ from the expected!");
    }

    @Test
    public void testGetLongestMovieReturnsTheCorrectMovie() {
        NetflixRecommender netflixRecommender = getSample();
        Content longestMovie = netflixRecommender.getTheLongestMovie();

        assertEquals(expectedContent.get(0), longestMovie, "The returned movie is not the longest one!");
    }

    @Test
    public void testGetLongestMovieThrowsWhenThereAreNotMoviesAdded() {
        NetflixRecommender netflixRecommender = new NetflixRecommender(new StringReader(""));

        assertThrows(NoSuchElementException.class, netflixRecommender::getTheLongestMovie,
                "NoSuchElementException was expected when getting the longest movie from an empty collection!");
    }

    @Test
    public void testGroupContentByType() {
        NetflixRecommender netflixRecommender = getSample();
        var groupedContent = netflixRecommender.groupContentByType();

        List<Content> movies = List.of(expectedContent.get(0), expectedContent.get(1), expectedContent.get(3));
        List<Content> shows = List.of(expectedContent.get(2));
        Map<ContentType, HashSet<Content>> expected = new TreeMap<>() {
            {
                put(ContentType.MOVIE, new HashSet<>(movies));
                put(ContentType.SHOW, new HashSet<>(shows));
            }
        };

        for (ContentType type : expected.keySet()) {
            assertIterableEquals(expected.get(type), groupedContent.get(type),
                    "Incorrect grouping!");
        }
    }

    @Test
    public void testGetSimilarContent() {
        NetflixRecommender netflixRecommender = getSample();
        List<Content> expectedOrder = List.of(expectedContent.get(1), expectedContent.get(3), expectedContent.get(0));

        assertIterableEquals(expectedOrder, netflixRecommender.getSimilarContent(expectedContent.get(1)));
    }
}
