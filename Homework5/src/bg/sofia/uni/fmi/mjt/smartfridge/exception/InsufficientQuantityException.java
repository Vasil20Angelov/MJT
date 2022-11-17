package bg.sofia.uni.fmi.mjt.smartfridge.exception;

public class InsufficientQuantityException extends Exception {
    public InsufficientQuantityException() {
        super();
    }

    public InsufficientQuantityException(String message) {
        super(message);
    }
}
