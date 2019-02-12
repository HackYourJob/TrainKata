import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<PlaceId> placesLibres = trainTopologie.getPlacesLibres()
                                            .stream()
                                            .limit(nbPlaces)
                                            .map(place -> place.getPlaceId())
                                            .collect(Collectors.toList());

        if (placesLibres.size() == nbPlaces) {
            ReservationId idReservation = reservationReferenceService.getNewReference();
            Reservation reservation = new Reservation(idReservation, placesLibres);
            reservationService.transmettre(reservation);
            return Optional.ofNullable(reservation);
        } else {
            return Optional.empty();
        }

    }
}
