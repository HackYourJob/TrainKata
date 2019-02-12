public class Reserver {
    private final TrainTopologyService trainTopologyService;
    private final ReservationReferenceService reservationReferenceService;
    private final ReservationService reservationService;

    public Reserver(TrainTopologyService trainTopologyService, ReservationReferenceService reservationReferenceService, ReservationService reservationService) {

        this.trainTopologyService = trainTopologyService;
        this.reservationReferenceService = reservationReferenceService;
        this.reservationService = reservationService;
    }

    public Reservation execute(String trainId, int nbPlaces) {
        return null;
    }
}
