public class Reserver {
    private final ITrainTopologyService trainTopologyService;
    private final IReservationReferenceService reservationReferenceService;
    private final IReservationService reservationService;

    public Reserver(ITrainTopologyService trainTopologyService, IReservationReferenceService reservationReferenceService, IReservationService reservationService) {

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
