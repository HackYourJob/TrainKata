package domain;

import java.util.Objects;

public class SeatCount {

    final int count;

    public SeatCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatCount seatCount = (SeatCount) o;
        return count == seatCount.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count);
    }

    @Override
    public String toString() {
        return String.valueOf(count);
    }
}
