package bg.sofia.uni.fmi.mjt.coinAPI;

import bg.sofia.uni.fmi.mjt.dto.Asset;
import bg.sofia.uni.fmi.mjt.exceptions.http.BadRequestException;
import bg.sofia.uni.fmi.mjt.exceptions.http.NoDataException;
import bg.sofia.uni.fmi.mjt.exceptions.http.NoPrivilegesException;
import bg.sofia.uni.fmi.mjt.exceptions.http.TooManyRequestsException;
import bg.sofia.uni.fmi.mjt.exceptions.http.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CoinClientTest {

    @Mock
    private HttpClient httpClientMock = Mockito.mock(HttpClient.class);

    @Mock
    private HttpResponse<String> httpResponseMock = Mockito.mock(HttpResponse.class);

    private CoinClient coinClient = new CoinClient(httpClientMock);

    private String getSampleDataInJsonFormat() {
        return "[{\"asset_id\":\"BTC\",\"name\":\"Bitcoin\",\"price_usd\":43000.0,\"type_is_crypto\":1}," +
                "{\"asset_id\":\"ETH\",\"name\":\"Ethereum\",\"price_usd\":3200.0,\"type_is_crypto\":1}]";
    }

    @BeforeEach
    public void setHttpClientMock() throws IOException, InterruptedException {
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
    }

    @Test
    public void testGetOfferingsListWhenRequestThrows() throws IOException, InterruptedException {
        when(httpClientMock.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> coinClient.getOfferingsList(),
                "Expected RuntimeException to be thrown when request fails!");
    }

    @Test
    public void testGetOfferingsWithSuccessfulOperation() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(httpResponseMock.body()).thenReturn(getSampleDataInJsonFormat());

        List<Asset> expected = List.of(
                new Asset("BTC", "Bitcoin", 43000, 1),
                new Asset("ETH", "Ethereum", 3200, 1));

        List<Asset> result = coinClient.getOfferingsList();

        assertIterableEquals(expected, result,
                "Expected RuntimeException to be thrown when request fails!");
    }

    @Test
    public void testGetOfferingsListWithBadRequestError() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);

        assertThrows(BadRequestException.class, () -> coinClient.getOfferingsList(),
                "Expected BadRequestException to be thrown when Bad request error code returned!");
    }

    @Test
    public void testGetOfferingsListWithUnauthorizedError() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);

        assertThrows(UnauthorizedException.class, () -> coinClient.getOfferingsList(),
                "Expected UnauthorizedException to be thrown when Unauthorized error code returned!");
    }

    @Test
    public void testGetOfferingsListWithForbiddenError() {
        when(httpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_FORBIDDEN);

        assertThrows(NoPrivilegesException.class, () -> coinClient.getOfferingsList(),
                "Expected NoPrivilegesException to be thrown when forbidden error code returned!");
    }

    @Test
    public void testGetOfferingsListWithTooManyRequestsError() {
        when(httpResponseMock.statusCode()).thenReturn(429);

        assertThrows(TooManyRequestsException.class, () -> coinClient.getOfferingsList(),
                "Expected TooManyRequestsException to be thrown when too many requests are made!");
    }

    @Test
    public void testGetOfferingsListWithNoDataError() {
        when(httpResponseMock.statusCode()).thenReturn(550);

        assertThrows(NoDataException.class, () -> coinClient.getOfferingsList(),
                "Expected NoDataException to be thrown when no data error code returned!");
    }

    @Test
    public void testGetOfferingsListWithUnknownError() {
        when(httpResponseMock.statusCode()).thenReturn(-1);

        assertThrows(RuntimeException.class, () -> coinClient.getOfferingsList(),
                "Expected RuntimeException to be thrown when unknown error code returned!");
    }
}
