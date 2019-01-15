package infra;

import java.util.Objects;

public class Seat {
    public final String coach;
    public final int seatNumber;
    public final int bookingReference;

    public Seat(String coach, int seatNumber, int bookingReference) {
        this.coach = coach;
        this.seatNumber = seatNumber;
        this.bookingReference = bookingReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return seatNumber == seat.seatNumber &&
                bookingReference == seat.bookingReference &&
                Objects.equals(coach, seat.coach);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coach, seatNumber, bookingReference);
    }
}