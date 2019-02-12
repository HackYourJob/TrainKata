import java.util.Optional;

public class Reserver {
    private final ITrainTopologyService trainTopologyService;
    private final IReservationReferenceService reservationReferenceService;
    private final IReservationService reservationService;

    public Reserver(ITrainTopologyService trainTopologyService,
                    IReservationReferenceService reservationReferenceService,
                    IReservationService reservationService) {

        this.trainTopologyService = trainTopologyService;
        this.reservationReferenceService = reservationReferenceService;
        this.reservationService = reservationService;
    }

    public Optional<Reservation> execute(TrainId trainId, int nbPlaces) {
        TrainTopologie trainTopologie = trainTopologyService.get(trainId);
        Optional<PlaceId> placeId =
                trainTopologie
                        .getPlacesLibres()
                        .stream()
                        .findFirst()
                        .map(place -> place.getPlaceId());

        if(placeId.isPresent()){
            ReservationId idReservation = reservationReferenceService.getNewReference();
            Reservation reservation = new Reservation(placeId.get(), idReservation);
            reservationService.transmettre(reservation);
            return Optional.ofNullable(reservation);
        } else {
            return Optional.empty();
        }

    }
}
