public class Reserver {
    private final TrainTopologyService trainTopologyService;
    private final ReservationReferenceService reservationReferenceService;
    private final ReservationService reservationService;

    public Reserver(TrainTopologyService trainTopologyService, ReservationReferenceService reservationReferenceService, ReservationService reservationService) {

        this.trainTopologyService = trainTopologyService;
        this.reservationReferenceService = reservationReferenceService;
        this.reservationService = reservationService;
    }

    public Reservation execute(TrainId trainId, int nbPlaces) {
        TrainTopologie trainTopologie = trainTopologyService.get(trainId);
        PlaceId placeId = trainTopologie.getPlaces().get(0).getPlaceId();
        ReservationId idReservation = reservationReferenceService.getNewReference();
        return new Reservation(placeId, idReservation);
    }
}
