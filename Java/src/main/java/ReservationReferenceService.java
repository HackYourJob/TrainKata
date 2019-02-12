public class ReservationReferenceService {
    private ReservationId idDeReservation;

    public ReservationReferenceService(ReservationId idDeReservation) {
        this.idDeReservation = idDeReservation;
    }

    public ReservationId getNewReference() {
        return idDeReservation;
    }
}
