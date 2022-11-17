package bg.sofia.uni.fmi.mjt.flightscanner.airport;

public record Airport(String id) implements Comparable<Airport> {
    public Airport {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Invalid airport ID");
        }
    }

    @Override
    public int compareTo(Airport o) {
        return id.compareTo(o.id());
    }
}
