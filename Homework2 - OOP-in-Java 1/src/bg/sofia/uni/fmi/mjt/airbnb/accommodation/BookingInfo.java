package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;
import java.time.Duration;
import java.time.LocalDateTime;

public class BookingInfo implements Bookable{

    private final String Id;
    private Location location;
    private long nights;
    private double pricePerNight;
    private boolean booked;

    public BookingInfo(Location location, double pricePerNight, String Id) {
        this.Id = Id;
        this.location = location;
        this.pricePerNight = pricePerNight;
        booked = false;
    }


    public Location getLocation() {
        return location;
    }

    public boolean isBooked() {
        return booked;
    }

    public boolean book(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (!canBeBooked(checkIn, checkOut)) {
            return false;
        }

        nights = Duration.between(checkIn, checkOut).toDays();
        booked = true;
        return true;
    }

    private boolean canBeBooked(LocalDateTime checkIn, LocalDateTime checkOut) {
        if (isBooked() || checkIn == null || checkOut == null) {
            return false;
        }

        if (checkIn.isBefore(LocalDateTime.now()) || !checkIn.isBefore(checkOut)) {
            return false;
        }

        return true;
    }

    public double getTotalPriceOfStay() {
        if (isBooked()) {
            return nights * pricePerNight;
        }

        return 0.0;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public String getId() {
        return Id;
    }
}
