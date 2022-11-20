package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class Hotel extends BookingInfo {

    public static int sequentialNumber = 0;
    private static final String prefixID = "HOT-";

    public Hotel(Location location, double pricePerNight) {
        super(location, pricePerNight, prefixID + sequentialNumber);
        sequentialNumber++;
    }
}
