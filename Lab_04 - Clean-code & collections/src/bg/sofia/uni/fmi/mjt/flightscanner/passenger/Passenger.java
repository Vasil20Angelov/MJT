package bg.sofia.uni.fmi.mjt.flightscanner.passenger;

public record Passenger(String id, String name, Gender gender) {
    public Passenger {
        if (id.isBlank() || name.isBlank() || gender == null) {
            throw new IllegalArgumentException("Invalid passenger");
        }
    }
}
