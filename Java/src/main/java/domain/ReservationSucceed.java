package domain;

import java.util.List;

public class ReservationSucceed implements ReservationDomainEvent{
    public final TrainId trainId;
    public final String bookingId;
    public final String coachId;
    public final List<Seat> seats;

    public ReservationSucceed(TrainId trainId, String bookingId, String coachId, List<Seat> seats) {
        this.trainId = trainId;
        this.bookingId = bookingId;
        this.coachId = coachId;
        this.seats = seats;
    }
}
