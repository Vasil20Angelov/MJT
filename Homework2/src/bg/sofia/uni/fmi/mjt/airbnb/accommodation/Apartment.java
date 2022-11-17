package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class Apartment extends BookingInfo {

    public static int sequentialNumber = 0;
    private static final String prefixID = "APA-";

    public Apartment(Location location, double pricePerNight) {
        super(location, pricePerNight, prefixID + sequentialNumber);
        sequentialNumber++;
    }
}
