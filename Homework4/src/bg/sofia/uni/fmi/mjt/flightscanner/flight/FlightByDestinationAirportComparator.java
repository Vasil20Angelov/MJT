package bg.sofia.uni.fmi.mjt.flightscanner.flight;

import java.util.Comparator;

public class FlightByDestinationAirportComparator implements Comparator<Flight> {
    @Override
    public int compare(Flight flight1, Flight flight2) {
        return flight1.getTo().compareTo(flight2.getTo());
    }
}
