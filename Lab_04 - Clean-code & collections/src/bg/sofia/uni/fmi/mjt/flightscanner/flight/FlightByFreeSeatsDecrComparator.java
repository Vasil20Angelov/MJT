package bg.sofia.uni.fmi.mjt.flightscanner.flight;

import java.util.Comparator;

public class FlightByFreeSeatsDecrComparator implements Comparator<Flight> {
    @Override
    public int compare(Flight flight1, Flight flight2) {
        return Integer.compare(flight2.getFreeSeatsCount(), flight1.getFreeSeatsCount());
    }
}
