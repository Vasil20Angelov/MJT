package bg.sofia.uni.fmi.mjt.exceptions;

public class PasswordEncryptionException extends RuntimeException {
    public PasswordEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
