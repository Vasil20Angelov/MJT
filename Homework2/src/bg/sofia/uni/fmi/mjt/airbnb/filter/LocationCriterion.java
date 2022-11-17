package bg.sofia.uni.fmi.mjt.airbnb.filter;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

public class LocationCriterion implements Criterion {

    private Location currentLocation;
    private double maxDistance;

    public LocationCriterion(Location currentLocation, double maxDistance) {
        this.currentLocation = currentLocation;
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean check(Bookable bookable) {
        if (bookable == null) {
            return false;
        }

        //  (x - center_x)² + (y - center_y)² <= radius²  =>  Point(x,y) is in/on the circle
        double x = bookable.getLocation().getX() - currentLocation.getX();
        double y = bookable.getLocation().getY() - currentLocation.getY();
        if ((x * x) + (y * y) <= maxDistance * maxDistance) {
            return true;
        }

        return false;
    }
}
