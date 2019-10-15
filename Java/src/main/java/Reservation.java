import java.util.List;

public class Reservation {

    public final TrainId trainId;
    public final BookingReference bookingReference;
    public final List<Seat> seats;

    public Reservation(TrainId trainId, BookingReference bookingReference, List<Seat> seats) {
        this.trainId = trainId;
        this.bookingReference = bookingReference;
        this.seats = seats;
    }

}
