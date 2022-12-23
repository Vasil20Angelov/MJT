package bg.sofia.uni.fmi.mjt.netflix;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ContentTest {

    @Test
    public void creationOfContentByValidLineWorksCorrectly() {
        String line = "tm84618,Taxi Driver,MOVIE,Some descr.,1976,114,['drama'; 'crime'],-1,tt0075314,8.2,808582.0";
        Content actual = Content.of(line);

        List<String> genres = new ArrayList<>() {
            {
                add("drama");
                add("crime");
            }
        };

        Content expected = new Content("tm84618","Taxi Driver",ContentType.MOVIE, "Some descr.",
                1976, 114, genres, -1, "tt0075314", 8.2, 808582.0);

        assertEquals(expected, actual, "Invalid generation of content by given line");
    }

    @Test
    public void creationOfContentReturnsNullIfTheGivenLineIsNotInCorrectFormat() {
        String line = "tm84618,Taxi Driver,MOVIE,-1,tt0075314,8.2,808582.0";
        assertNull(Content.of(line), "The content should be null");
    }

//    @Test
//    public void testSimilarity() {
//        NetflixRecommender netflixRecommender = getSample();
//        List<String> genres1 = new ArrayList<>() {
//            {
//                add("drama");
//                add("crime");
//                add("thriller");
//                add("action");
//            }
//        };
//
//        List<String> genres2 = new ArrayList<>() {
//            {
//                add("drama");
//                add("crime");
//                add("comedy");
//            }
//        };
//
//        List<String> genres3 = new ArrayList<>() {
//            {
//                add("drama");
//                add("crime");
//                add("action");
//            }
//        };
//
//        Content c1 = new Content("tm1","Taxi Driver",ContentType.MOVIE, "Some descr.",
//                1976, 114, genres1, -1, "tt0075314", 8.2, 808582.0);
//
//        Content c2 = new Content("tm2","Taxi Driver",ContentType.MOVIE, "Some descr.",
//                1976, 114, genres2, -1, "tt0075314", 8.2, 808582.0);
//
//        Content c3 = new Content("tm3","Taxi Driver",ContentType.MOVIE, "Some descr.",
//                1976, 114, genres3, -1, "tt0075314", 8.2, 808582.0);

//        List<Content> expectedOrder = List.of(expectedContent.get(0), expectedContent.get(2), expectedContent.get(1));
//
//        assertIterableEquals(expectedOrder, netflixRecommender.getSimilarContent(expectedContent.get(1)));
//    }
}
