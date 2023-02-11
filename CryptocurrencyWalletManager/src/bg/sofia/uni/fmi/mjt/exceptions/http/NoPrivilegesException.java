package bg.sofia.uni.fmi.mjt.exceptions.http;

public class NoPrivilegesException extends RuntimeException {
    public NoPrivilegesException(String message) {
        super(message);
    }
}
