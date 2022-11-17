package bg.sofia.uni.fmi.mjt.flightscanner.exception;

public class FlightCapacityExceededException extends Exception {

    public FlightCapacityExceededException() {
        super();
    }

    public FlightCapacityExceededException(String message) {
        super(message);
    }
}
