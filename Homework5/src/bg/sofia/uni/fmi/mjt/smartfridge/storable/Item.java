package bg.sofia.uni.fmi.mjt.smartfridge.storable;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.type.StorableType;

import java.time.LocalDate;
import java.util.Objects;

public class Item implements Storable {
    private StorableType type;
    private String name;
    private LocalDate expirationDate;

    public Item(StorableType type, String name, LocalDate expirationDate) {
        this.type = type;
        this.name = name;
        this.expirationDate = expirationDate;
    }

    @Override
    public StorableType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDate getExpiration() {
        return expirationDate;
    }

    @Override
    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return type == item.type && Objects.equals(name, item.name)
                && Objects.equals(expirationDate, item.expirationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, expirationDate);
    }
}
