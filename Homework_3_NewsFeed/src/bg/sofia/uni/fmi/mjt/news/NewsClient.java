package bg.sofia.uni.fmi.mjt.news;

import bg.sofia.uni.fmi.mjt.news.dto.News;
import bg.sofia.uni.fmi.mjt.news.exceptions.HttpException;
import bg.sofia.uni.fmi.mjt.news.exceptions.NewsClientException;
import bg.sofia.uni.fmi.mjt.news.query.NewsQuery;
import bg.sofia.uni.fmi.mjt.news.query.NewsQueryParser;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NewsClient {
    private static final String DEFAULT_KEY = "Get key from: https://newsapi.org//register";

    private static final String API_KEY = "&apiKey=";
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "newsapi.org";
    private static final String API_ENDPOINT_PATH = "/v2/top-headlines";

    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final Gson GSON = new Gson();

    private final HttpClient newsHttpClient;
    private final NewsQueryParser newsQueryParser;
    private final String key;

    public NewsClient(HttpClient newsHttpClient, NewsQueryParser newsQueryParser) {
        this(newsHttpClient, newsQueryParser, DEFAULT_KEY);
    }

    public NewsClient(HttpClient newsHttpClient, NewsQueryParser newsQueryParser, String apiKey) {
        this.newsHttpClient = newsHttpClient;
        this.newsQueryParser = newsQueryParser;
        this.key = apiKey;
    }

    public News getNews(NewsQuery query) throws NewsClientException {
        HttpResponse<String> response;
        String endPointQuery = newsQueryParser.parse(query) + API_KEY + key;

        try {
            URI uri = new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH, endPointQuery, null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            response = newsHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new NewsClientException("Could not retrieve any news!", e);
        }

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            handleErrorStatusCodes(response.statusCode());
        }

        return GSON.fromJson(response.body(), News.class);
    }

    private void handleErrorStatusCodes(int statusCode) {
        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            throw new HttpException("Bad Request\n" +
                    "The request was unacceptable, due to a missing or misconfigured parameter.");
        }

        if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new HttpException("Unauthorized!\nAPI key was missing from the request, or wasn't correct.");
        }

        if (statusCode == HTTP_TOO_MANY_REQUESTS) {
            throw new HttpException("Too Many Requests\n" +
                    "Too many requests within a window of time and have been rate limited.");
        }

        if (statusCode == HttpURLConnection.HTTP_SERVER_ERROR) {
            throw new HttpException("Server Error!\nSomething went wrong on the side.");
        }

        throw new HttpException("Unexpected status code!");
    }
}
