package bg.sofia.uni.fmi.mjt.flightscanner.flight;

import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.exception.FlightCapacityExceededException;
import bg.sofia.uni.fmi.mjt.flightscanner.exception.InvalidFlightException;
import bg.sofia.uni.fmi.mjt.flightscanner.passenger.Passenger;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class RegularFlight implements Flight {

    private final String flightId;
    private final Airport from;
    private final Airport to;
    private final int totalCapacity;
    private List<Passenger> passengers;

    private RegularFlight(String flightId, Airport from, Airport to, int totalCapacity) {
        this.flightId = flightId;
        this.from = from;
        this.to = to;
        this.totalCapacity = totalCapacity;
        passengers = new ArrayList<>();
    }

    public static RegularFlight of(String flightId, Airport from, Airport to, int totalCapacity) {
        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException("Invalid flight ID");
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException("Invalid airports");
        }

        if (totalCapacity < 0) {
            throw new IllegalArgumentException("Total capacity cannot be negative");
        }

        if (from.equals(to)) {
            throw new InvalidFlightException("The flight destination must be different from the starting airport");
        }

        return new RegularFlight(flightId, from, to, totalCapacity);
    }

    @Override
    public Airport getFrom() {
        return from;
    }

    @Override
    public Airport getTo() {
        return to;
    }

    @Override
    public void addPassenger(Passenger passenger) throws FlightCapacityExceededException {
        if (passenger == null) {
            return;
        }

        if (getFreeSeatsCount() < 1) {
            throw new FlightCapacityExceededException();
        }

        passengers.add(passenger);
    }

    @Override
    public void addPassengers(Collection<Passenger> passengers) throws FlightCapacityExceededException {
        if (passengers == null) {
            return;
        }

        if (getFreeSeatsCount() < passengers.size()) {
            throw new FlightCapacityExceededException();
        }

        this.passengers.addAll(passengers);
    }

    @Override
    public Collection<Passenger> getAllPassengers() {
        return List.copyOf(passengers);
    }

    @Override
    public int getFreeSeatsCount() {
        return totalCapacity - passengers.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegularFlight that = (RegularFlight) o;
        return totalCapacity == that.totalCapacity && Objects.equals(flightId, that.flightId)
                && Objects.equals(from, that.from) && Objects.equals(to, that.to)
                && Objects.equals(passengers, that.passengers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId, from, to, totalCapacity, passengers);
    }
}
