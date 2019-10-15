import java.util.List;

public class Reservation {

    public final String trainId;
    public final String bookingReference;
    public final List<Seat> seats;

    public Reservation(String trainId, String bookingReference, List<Seat> seats) {
        this.trainId = trainId;
        this.bookingReference = bookingReference;
        this.seats = seats;
    }

}
