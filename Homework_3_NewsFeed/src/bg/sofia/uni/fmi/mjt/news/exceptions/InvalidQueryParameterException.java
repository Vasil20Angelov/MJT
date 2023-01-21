package bg.sofia.uni.fmi.mjt.news.exceptions;

public class InvalidQueryParameterException extends Exception {
    public InvalidQueryParameterException() {
    }

    public InvalidQueryParameterException(String message) {
        super(message);
    }
}
