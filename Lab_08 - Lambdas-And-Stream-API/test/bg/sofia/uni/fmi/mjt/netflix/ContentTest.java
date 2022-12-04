package bg.sofia.uni.fmi.mjt.netflix;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

}
