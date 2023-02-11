package bg.sofia.uni.fmi.mjt.exceptions.http;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String message) {
        super(message);
    }
}
