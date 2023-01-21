package bg.sofia.uni.fmi.mjt.news;

import bg.sofia.uni.fmi.mjt.news.dto.Article;
import bg.sofia.uni.fmi.mjt.news.dto.News;
import bg.sofia.uni.fmi.mjt.news.dto.ObjectSource;
import bg.sofia.uni.fmi.mjt.news.exceptions.HttpException;
import bg.sofia.uni.fmi.mjt.news.exceptions.NewsClientException;
import bg.sofia.uni.fmi.mjt.news.query.NewsQuery;
import bg.sofia.uni.fmi.mjt.news.query.NewsQueryParser;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NewsClientTest {

    @Mock
    HttpClient httpClientMock;

    @Mock
    NewsQuery newsQueryMock;

    @Mock
    NewsQueryParser newsQueryParserMock;

    @Mock
    HttpResponse<String> httpResponseMock;

    @InjectMocks
    NewsClient newsClient;

    private void setMocks(int responseStatusCode) throws IOException, InterruptedException {
        when(newsQueryParserMock.parse(any(NewsQuery.class))).thenReturn("&q=abv");
        when(httpResponseMock.statusCode()).thenReturn(responseStatusCode);
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }

    private Article getArticle() {
        ObjectSource source = new ObjectSource("15", "bbc");
        return new Article(source, "John", "War", "In Europe", "/some/path"
                , "some/path2", "yesterday", "asdfasgf");
    }
    @Test
    public void testGetNewsHandlesExceptionFromSendingRequest() throws IOException, InterruptedException {
        when(newsQueryParserMock.parse(any(NewsQuery.class))).thenReturn("&q=abv");
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(new RuntimeException());

        assertThrows(NewsClientException.class, () -> newsClient.getNews(newsQueryMock),
                "Expected NewsClientException to be thrown!");
    }

    @Test
    public void testGetNewsHandles_HTTP_BAD_REQUEST() throws IOException, InterruptedException {
        setMocks(HttpURLConnection.HTTP_BAD_REQUEST);

        assertThrows(HttpException.class, () -> newsClient.getNews(newsQueryMock),
                "Expected HttpException to be thrown!");
    }

    @Test
    public void testGetNewsHandles_HTTP_UNAUTHORIZED() throws IOException, InterruptedException {
        setMocks(HttpURLConnection.HTTP_UNAUTHORIZED);

        assertThrows(HttpException.class, () -> newsClient.getNews(newsQueryMock),
                "Expected HttpException to be thrown!");
    }

    @Test
    public void testGetNewsHandles_HTTP_TOO_MANY_REQUESTS() throws IOException, InterruptedException {
        setMocks(429);

        assertThrows(HttpException.class, () -> newsClient.getNews(newsQueryMock),
                "Expected HttpException to be thrown!");
    }

    @Test
    public void testGetNewsHandles_HTTP_SERVER_ERROR() throws IOException, InterruptedException {
        setMocks(HttpURLConnection.HTTP_SERVER_ERROR);

        assertThrows(HttpException.class, () -> newsClient.getNews(newsQueryMock),
                "Expected HttpException to be thrown!");
    }

    @Test
    public void testGetNewsHandlesUnexpectedStatusCode() throws IOException, InterruptedException {
        setMocks(600);

        assertThrows(HttpException.class, () -> newsClient.getNews(newsQueryMock),
                "Expected HttpException to be thrown!");
    }

    @Test
    public void testGetNewsReturnsTheCorrectObject() throws IOException, InterruptedException, NewsClientException {
        setMocks(HttpURLConnection.HTTP_OK);

        News expected = new News("OK", 20, new Article[] {getArticle()});
        String jsonFormat = new Gson().toJson(expected);

        when(httpResponseMock.body()).thenReturn(jsonFormat);

        News result = newsClient.getNews(newsQueryMock);

        assertEquals(expected, result, "Unexpected returned object!");
    }
}
