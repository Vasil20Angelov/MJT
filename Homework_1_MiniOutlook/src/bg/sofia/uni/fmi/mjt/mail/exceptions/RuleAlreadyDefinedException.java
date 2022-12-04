package bg.sofia.uni.fmi.mjt.mail.exceptions;

public class RuleAlreadyDefinedException extends RuntimeException {
    public RuleAlreadyDefinedException() {
    }

    public RuleAlreadyDefinedException(String message) {
        super(message);
    }
}
