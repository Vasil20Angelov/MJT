package bg.sofia.uni.fmi.mjt.airbnb;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.filter.Criterion;

public class Airbnb implements AirbnbAPI {

    private final Bookable[] accommodations;

    public Airbnb(Bookable[] accommodations) {
        this.accommodations = accommodations;
    }

    @Override
    public Bookable findAccommodationById(String id) {
        if (id == null || id.length() < 5) {
            return null;
        }

        for (Bookable accommodation : accommodations) {
            if (accommodation.getId().equalsIgnoreCase(id)) {
                return accommodation;
            }
        }

        return null;
    }

    @Override
    public double estimateTotalRevenue() {
        double totalRevenue = 0;
        if (accommodations != null) {
            for (Bookable accommodation : accommodations) {
                totalRevenue += accommodation.getTotalPriceOfStay();
            }
        }

        return totalRevenue;
    }

    @Override
    public long countBookings() {
        long bookingsCount = 0;
        if (accommodations != null) {
            for (Bookable accommodation : accommodations) {
                if (accommodation.isBooked()) {
                    bookingsCount++;
                }
            }
        }

        return bookingsCount;
    }

    @Override
    public Bookable[] filterAccommodations(Criterion... criteria) {

        if (accommodations == null || criteria == null) {
            return null;
        }

        if (criteria.length == 0) {
            return accommodations;
        }

        int filteredAccommodationsCount = 0;
        boolean[] meetRequirements = new boolean[accommodations.length];
        for (int i = 0; i < accommodations.length; ++i) {
            boolean passesCriteria = true;
            for (Criterion criterion : criteria) {
                if (!criterion.check(accommodations[i])) {
                    passesCriteria = false;
                    break;
                }
            }
            if (passesCriteria) {
                filteredAccommodationsCount++;
            }
            meetRequirements[i] = passesCriteria;
        }

        int index = 0;
        Bookable[] filteredAccommodations = new Bookable[filteredAccommodationsCount];
        for (int i = 0; i < meetRequirements.length; ++i) {
            if (meetRequirements[i]) {
                filteredAccommodations[index++] = accommodations[i];
            }
        }

        return  filteredAccommodations;
    }
}
