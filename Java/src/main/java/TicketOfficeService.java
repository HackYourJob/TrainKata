import external.bookingReference.BookingReferenceClient;
import external.trainData.Topologie;
import external.trainData.TrainDataClient;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    public ReservationResponseDto makeReservation(ReservationRequestDto request) {
        Topologie topology = this.trainDataClient.getTopology(request.trainId);

        // TODO
        // - Règle de validation des 70%
        // - Wrapper Topologie en classe métier Train+Coach+seat qui contiendront les règles métier
        // - Séparer le code du domaine du code d'infra (Archi Hexa)

        Map<String, List<Topologie.TopologieSeat>> seatsByCoach = topology.seats.keySet()
                .stream()
                .map(seatId -> topology.seats.get(seatId))
                .collect(Collectors.groupingBy(Topologie.TopologieSeat::getCoach));

        Optional<List<Topologie.TopologieSeat>> elegibleCoach = seatsByCoach.values().stream()
                .filter(seatsList -> seatsList.stream()
                        .filter(Topologie.TopologieSeat::isAvailable).count() >= request.seatCount)
                .findFirst();

        if(elegibleCoach.isPresent()) {
            List<ReservationResponseDto.Seat> seats = elegibleCoach.get().stream()
                    .filter(Topologie.TopologieSeat::isAvailable)
                    .limit(request.seatCount)
                    .map(topologieSeat -> new ReservationResponseDto.Seat(topologieSeat.coach, topologieSeat.seat_number))
                    .collect(Collectors.toList());

            String bookingReference = bookingReferenceClient.generateBookingReference();

            return new ReservationResponseDto(request.trainId, seats, bookingReference);
        }
        return new ReservationResponseDto(request.trainId, new ArrayList<>(), "");

    }
}