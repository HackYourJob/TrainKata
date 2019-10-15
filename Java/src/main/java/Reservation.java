import java.util.List;

public class Reservation {

    public final TrainId trainId;
    public final String bookingReference;
    public final List<Seat> seats;

    public Reservation(TrainId trainId, String bookingReference, List<Seat> seats) {
        this.trainId = trainId;
        this.bookingReference = bookingReference;
        this.seats = seats;
    }

}
