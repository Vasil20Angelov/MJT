package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class Villa extends BookingInfo {

    public static int sequentialNumber = 0;
    private static final String prefixID = "VIL-";

    public Villa(Location location, double pricePerNight) {
        super(location, pricePerNight, prefixID + sequentialNumber);
        sequentialNumber++;
    }
}
