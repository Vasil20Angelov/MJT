package bg.sofia.uni.fmi.mjt.news.exceptions;

public class HttpException extends RuntimeException {
    public HttpException() {
    }

    public HttpException(String message) {
        super(message);
    }
}
