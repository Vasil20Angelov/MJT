package bg.sofia.uni.fmi.mjt.news.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


public class NewsQueryParserTest {

    @Mock
    NewsQuery newsQueryMock = Mockito.mock(NewsQuery.class);

    @BeforeEach
    public void setNewsQueryMock() {
        when(newsQueryMock.getKeyWords()).thenReturn("izbori");
        when(newsQueryMock.getPage()).thenReturn("3");
        when(newsQueryMock.getCountry()).thenReturn("bg");
        when(newsQueryMock.getPageSize()).thenReturn(null);
        when(newsQueryMock.getCategory()).thenReturn(null);
    }

    @Test
    public void testParseWithSomeParametersMissing() {
        NewsQueryParser parser = new NewsQueryParser();
        String result = parser.parse(newsQueryMock);
        String expected = "q=izbori&country=bg&page=3";

        assertEquals(expected, result, "Unexpected parse result!");
    }

    @Test
    public void testParseWithAllParametersFilled() {
        when(newsQueryMock.getPageSize()).thenReturn("20");
        when(newsQueryMock.getCategory()).thenReturn("politics");

        NewsQueryParser parser = new NewsQueryParser();
        String result = parser.parse(newsQueryMock);
        String expected = "q=izbori&country=bg&category=politics&pageSize=20&page=3";

        assertEquals(expected, result, "Unexpected parse result!");
    }
}
