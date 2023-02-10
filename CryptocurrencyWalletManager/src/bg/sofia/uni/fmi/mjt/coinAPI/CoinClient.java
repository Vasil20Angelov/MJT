package bg.sofia.uni.fmi.mjt.coinAPI;

import bg.sofia.uni.fmi.mjt.dto.Asset;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CoinClient {
    private static final String API_KEY = "7090DEDA-123B-447B-8549-13E19984B6EC";

    private static final String API_ENDPOINT_SCHEME = "http";
    private static final String API_ENDPOINT_HOST = "rest.coinapi.io";
    private static final String API_ENDPOINT_PATH = "/v1/assets";
    private static final String API_ENDPOINT_QUERY = "apikey=%s";
    private static final Gson GSON = new Gson();

    private final HttpClient coinHttpClient;
    private final String apiKey;

    public CoinClient(HttpClient weatherHttpClient) {
        this(weatherHttpClient, API_KEY);
    }

    public CoinClient(HttpClient weatherHttpClient, String apiKey) {
        this.coinHttpClient = weatherHttpClient;
        this.apiKey = apiKey;
    }

    public List<Asset> getOfferingsList() {
        HttpResponse<String> response;

        try {
            URI uri = new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, API_ENDPOINT_PATH,
                    API_ENDPOINT_QUERY.formatted(apiKey), null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

            response = coinHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve weather forecast", e);
        }
        List<Asset> assets = new ArrayList<>();
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            TypeToken type = new TypeToken<List<Asset>>() { };
            assets = GSON.fromJson(response.body(), type.getType());
        }

        return assets;
//        if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
//            throw new LocationNotFoundException("Could not find " + city);
//        }
//
//        throw new WeatherForecastClientException("Unexpected response code from weather forecast service");
    }
}
