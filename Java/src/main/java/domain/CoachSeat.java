package domain;

import java.util.Objects;

public class CoachSeat {

    public final Seat seat;
    public final SeatAvailability availability;

    public CoachSeat(Seat seat, SeatAvailability availability) {
        this.seat = seat;
        this.availability = availability;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoachSeat coachSeat = (CoachSeat) o;
        return Objects.equals(seat, coachSeat.seat) &&
                availability == coachSeat.availability;
    }

    @Override
    public int hashCode() {
        return Objects.hash(seat, availability);
    }
}
