package bg.sofia.uni.fmi.mjt.exceptions.http;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
