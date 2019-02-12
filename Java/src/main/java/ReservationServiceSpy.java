public class ReservationServiceSpy implements IReservationService{
    public Reservation reservation;

    public void transmettre(Reservation reservation) {
        this.reservation = reservation;
    }
}
