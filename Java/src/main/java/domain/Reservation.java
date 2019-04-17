package domain;

import java.util.List;

public class Reservation {
    public final TrainId trainId;
    public final String bookingId;
    public final List<Seat> seats;

    public Reservation(TrainId trainId, String bookingId, List<Seat> seats) {
        this.trainId = trainId;
        this.bookingId = bookingId;
        this.seats = seats;
    }
}
