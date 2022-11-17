package bg.sofia.uni.fmi.mjt.flightscanner;

import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.Flight;
import bg.sofia.uni.fmi.mjt.flightscanner.flight.FlightByDestinationAirportComparator;

import java.util.*;

public class FlightScanner implements FlightScannerAPI {

    private Set<Flight> flights = new HashSet<>();

    private Map<Airport, Set<Flight>> graph = new TreeMap<>();

    @Override
    public void add(Flight flight) {
        if (flight == null) {
            throw new IllegalArgumentException("Invalid flight");
        }

        if (!flights.contains(flight)) {
            flights.add(flight);

            if (graph.containsKey(flight.getFrom())) {
                graph.get(flight.getFrom()).add(flight);
            } else {
                Set<Flight> edges = new HashSet<>();
                edges.add(flight);
                graph.put(flight.getFrom(), edges);
            }
        }
    }

    @Override
    public void addAll(Collection<Flight> flights) {
        if (flights == null) {
            throw new IllegalArgumentException("Invalid flights");
        }

        for (Flight flight : flights) {
            add(flight);
        }
    }

    @Override
    public List<Flight> searchFlights(Airport from, Airport to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Invalid airport");
        }

        if (to.equals(from)) {
            throw new IllegalArgumentException("The destination airport must be different from the starting airport");
        }

        if (graph.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Airport, Flight> predecessors = findFlightsToTargetAirportByBFS(from, to);
        return getFlightsSequence(from, to, predecessors);
    }

    private Map<Airport, Flight> findFlightsToTargetAirportByBFS(Airport from, Airport to) {
        Set<Airport> visited = new HashSet<>();
        visited.add(from);
        List<Airport> queue = new LinkedList<>();
        queue.add(from);
        Map<Airport, Flight> predecessors = new HashMap<>();

        while (!queue.isEmpty()) {
            Airport current = queue.remove(0);
            if (!graph.containsKey(current)) {
                continue;
            }

            for (Flight edge : graph.get(current)) {
                if (!visited.contains(edge.getTo())) {
                    predecessors.put(edge.getTo(), edge);
                    if (edge.getTo() == to) {
                        return predecessors;
                    }

                    visited.add(edge.getTo());
                    queue.add(edge.getTo());
                }
            }
        }

        return null;
    }

    private List<Flight> getFlightsSequence(Airport from, Airport to, Map<Airport, Flight> predecessors) {
        List<Flight> flightSequence = new ArrayList<>();

        if (predecessors != null) {
            Airport current = to;

            do {
                flightSequence.add(predecessors.get(current));
                current = predecessors.get(current).getFrom();
            } while (current != from);

            Collections.reverse(flightSequence);
        }

        return flightSequence;
    }

    @Override
    public List<Flight> getFlightsSortedByFreeSeats(Airport from) {
        return getSortedFlights(from, new FlightByDestinationAirportComparator());
    }

    @Override
    public List<Flight> getFlightsSortedByDestination(Airport from) {
        return getSortedFlights(from, new FlightByDestinationAirportComparator());
    }

    private List<Flight> getSortedFlights(Airport from, Comparator<Flight> comparator) {
        if (from == null) {
            throw new IllegalArgumentException("Invalid airport");
        }

        PriorityQueue<Flight> orderedFlights = new PriorityQueue<>(comparator);
        for (Flight flight : flights) {
            if (flight.getFrom().equals(from)) {
                orderedFlights.add(flight);
            }
        }

        return List.copyOf(orderedFlights);
    }
}
