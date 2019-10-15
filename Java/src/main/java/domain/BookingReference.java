package domain;

import java.util.Objects;

public class BookingReference {
    public final String reference;

    public BookingReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingReference that = (BookingReference) o;
        return Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }
}
