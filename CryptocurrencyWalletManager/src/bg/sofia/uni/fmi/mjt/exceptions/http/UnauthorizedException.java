package bg.sofia.uni.fmi.mjt.exceptions.http;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
