package bg.sofia.uni.fmi.mjt.wallet;

import java.util.Objects;

public record Investment(double amount, double price) {

    private static final double DELTA = 0.000001;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Investment that = (Investment) o;
        return Math.abs(that.amount - amount) < DELTA && Math.abs(that.price - price) < DELTA;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, price);
    }
}
