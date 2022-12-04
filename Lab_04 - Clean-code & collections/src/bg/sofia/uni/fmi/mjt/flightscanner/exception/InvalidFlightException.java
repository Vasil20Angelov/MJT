package bg.sofia.uni.fmi.mjt.flightscanner.exception;

public class InvalidFlightException extends RuntimeException {

    public InvalidFlightException() {
        super();
    }

    public InvalidFlightException(String message) {
        super(message);
    }
}
