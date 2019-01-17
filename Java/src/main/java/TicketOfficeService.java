import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import domain.BookingReferenceClient;
import domain.Seat;
import domain.TrainDataClient;
import infra.in.ReservationRequestDto;
import infra.in.ReservationResponseDto;
import infra.out.Topologie;

public class TicketOfficeService {

    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    /**
     * // - Règle de validation des 70%
     * // - Wrapper Topologie en classe métier Train+Coach+seat qui contiendront les règles métier
     * // - Séparer le code du domaine du code d'infra (Archi Hexa)
     */
    public ReservationResponseDto makeReservation(ReservationRequestDto request) {
        List<Seat> trainSeats = this.trainDataClient.getTopology(request.trainId);

        Topologie topologie = new Topologie();
        topologie.seats = new LinkedHashMap<>();
        trainSeats.forEach(seat -> topologie.seats.put(seat.coach + seat.seatNumber, new Topologie.TopologieSeat(seat)));
        Optional<List<Topologie.TopologieSeat>> elegibleCoach = findEligibleCoach(request, topologie);

        if (!elegibleCoach.isPresent()) {
            return new ReservationResponseDto(request.trainId, new ArrayList<>(), "");
        }

        List<ReservationResponseDto.Seat> seats = getSeatsToReserve(request, elegibleCoach);

        String bookingReference = bookingReferenceClient.generateBookingReference();

        return new ReservationResponseDto(request.trainId, seats, bookingReference);
    }

    private List<ReservationResponseDto.Seat> getSeatsToReserve(ReservationRequestDto request, Optional<List<Topologie.TopologieSeat>> elegibleCoach) {
        return elegibleCoach.get().stream()
                    .filter(Topologie.TopologieSeat::isAvailable)
                    .limit(request.seatCount)
                    .map(topologieSeat -> new ReservationResponseDto.Seat(topologieSeat.coach, topologieSeat.seat_number))
                    .collect(Collectors.toList());
    }

    private Optional<List<Topologie.TopologieSeat>> findEligibleCoach(ReservationRequestDto request, Topologie topology) {
        Map<String, List<Topologie.TopologieSeat>> seatsByCoach = topology.seats.keySet()
                .stream()
                .map(seatId -> topology.seats.get(seatId))
                .collect(Collectors.groupingBy(Topologie.TopologieSeat::getCoach));

        return seatsByCoach.values().stream()
                .filter(seatsList -> seatsList.stream()
                        .filter(Topologie.TopologieSeat::isAvailable).count() >= request.seatCount)
                .findFirst();
    }
}
