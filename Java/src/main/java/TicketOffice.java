import java.util.ArrayList;
import java.util.List;

public class TicketOffice {

    private SeatsOptimizer seatsOptimizer;

    public TicketOffice(SeatsOptimizer seatsOptimizer) {
        this.seatsOptimizer = seatsOptimizer;
    }

    public Reservation makeReservation(ReservationRequest request) {
        List<Seat> seats = seatsOptimizer.getAvailableSeats(request.trainId, request.seatCount);

        Reservation reservation = new Reservation(request.trainId, new ArrayList<>(), "");
        return reservation;
    }

}