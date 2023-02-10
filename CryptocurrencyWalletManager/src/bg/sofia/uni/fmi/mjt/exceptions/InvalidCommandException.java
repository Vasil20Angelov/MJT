package bg.sofia.uni.fmi.mjt.exceptions;

public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
