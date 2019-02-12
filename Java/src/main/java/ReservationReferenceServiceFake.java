public class ReservationReferenceServiceFake implements IReservationReferenceService {
    private ReservationId idDeReservation;

    public ReservationReferenceServiceFake(ReservationId idDeReservation) {
        this.idDeReservation = idDeReservation;
    }

    @Override
    public ReservationId getNewReference() {
        return idDeReservation;
    }
}
