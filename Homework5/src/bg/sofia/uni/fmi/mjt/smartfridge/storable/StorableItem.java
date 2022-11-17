package bg.sofia.uni.fmi.mjt.smartfridge.storable;

import java.util.Objects;

public class StorableItem<E extends Storable> implements Comparable<StorableItem> {
    private E item;
    private int quantity;

    public StorableItem(E item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public E getItem() {
        return item;
    }

    @Override
    public int compareTo(StorableItem o) {
        if (this.item == null && o.item == null) {
            return 0;
        }

        if (this.item == null || o.item == null) {
            return -1;
        }

        if (this.item.getExpiration() == null) {
            return -1;
        }

        if (o.item.getExpiration() == null) {
            return 1;
        }

        return item.getExpiration().compareTo(o.item.getExpiration());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorableItem<?> that = (StorableItem<?>) o;
        return that.item.getName().equals(item.getName())
                && that.item.getExpiration().equals(item.getExpiration()) &&
                that.item.getType().equals(item.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(item);
    }
}
