package bg.sofia.uni.fmi.mjt.sentiment.utils.factories;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StopwordsFactoryTest {

    @Test
    public void testCreate() {
        String content = """
                a
                i
                I
                this
                tHAt
                """;

        Set<String> actual;
        try (StringReader reader = new StringReader(content)) {
            actual = StopwordsFactory.create(reader);
        }

        Set<String> expected = new HashSet<>() {
            {
                add("a");
                add("i");
                add("this");
                add("that");
            }
        };

        assertEquals(expected, actual,
                "The returned collection does not contain the expected values!");
    }
}
