package bg.sofia.uni.fmi.mjt.coin.api;

import bg.sofia.uni.fmi.mjt.dto.Asset;
import bg.sofia.uni.fmi.mjt.exceptions.http.BadRequestException;
import bg.sofia.uni.fmi.mjt.exceptions.http.NoDataException;
import bg.sofia.uni.fmi.mjt.exceptions.http.NoPrivilegesException;
import bg.sofia.uni.fmi.mjt.exceptions.http.TooManyRequestsException;
import bg.sofia.uni.fmi.mjt.exceptions.http.UnauthorizedException;
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
    private static final String API_KEY = "Put your api key here";
    private static final String API_ENDPOINT_SCHEME = "http";
    private static final String API_ENDPOINT_HOST = "rest.coinapi.io";
    private static final String API_ENDPOINT_PATH = "/v1/assets";
    private static final String API_ENDPOINT_QUERY = "apikey=%s";
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final int HTTP_NO_DATA = 550;
    private static final Gson GSON = new Gson();

    private final HttpClient coinHttpClient;
    private final String apiKey;

    public CoinClient(HttpClient coinHttpClient) {
        this(coinHttpClient, API_KEY);
    }

    public CoinClient(HttpClient coinHttpClient, String apiKey) {
        this.coinHttpClient = coinHttpClient;
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
            throw new RuntimeException("Could not retrieve stock exchange information!", e);
        }

        List<Asset> assets = new ArrayList<>();
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            TypeToken<List<Asset>> type = new TypeToken<>() { };
            assets = GSON.fromJson(response.body(), type.getType());
        } else {
            handleErrorCodes(response.statusCode());
        }

        return assets;
    }

    private void handleErrorCodes(int errorCode) {

        switch (errorCode) {
            case HttpURLConnection.HTTP_BAD_REQUEST
                -> throw new BadRequestException("There is something wrong with your request!");
            case HttpURLConnection.HTTP_UNAUTHORIZED
                -> throw new UnauthorizedException("API key is invalid!");
            case HttpURLConnection.HTTP_FORBIDDEN
                -> throw new NoPrivilegesException("API key does not have enough privileges to access this resource!");
            case HTTP_TOO_MANY_REQUESTS
                -> throw new TooManyRequestsException("Exceeded API key rate limits!");
            case HTTP_NO_DATA
                -> throw new NoDataException("Requested specific single item that is unavailable have at this moment!");
            default
                -> throw new RuntimeException("Error code: " + errorCode);
        }
    }
}
