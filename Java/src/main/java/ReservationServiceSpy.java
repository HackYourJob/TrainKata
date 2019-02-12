import java.util.Optional;

public class ReservationServiceSpy implements IReservationService{
    private Reservation reservation;

    public Optional<Reservation> getReservation() {
        return Optional.ofNullable(reservation);
    }

    public void transmettre(Reservation reservation) {
        this.reservation = reservation;
    }
}
