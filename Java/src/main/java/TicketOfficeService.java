import com.google.gson.Gson;

import java.util.*;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    // FIXME: move to infra
    public String makeReservation(ReservationRequestDto reservationRequest) {
        Optional<Reservation> reservation = makeReservation(reservationRequest.toDomainModel());

        if (reservation.isEmpty()) {
            return "{\"train_id\": \"" + reservationRequest.trainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }
        return "{" +
                "\"train_id\": \"" + reservation.get().trainId + "\", " +
                "\"booking_reference\": \"" + reservation.get().bookingReference.reference + "\", " +
                "\"seats\": [" + reservation.get().seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";
    }

    // FIXME : Trop grosse !
    // FIXME : Retourner une réservation
    public Optional<Reservation> makeReservation(ReservationRequest reservationRequest) {
        // FIXME : Retourner une topologie
        String topologie = trainDataClient.getTopology(reservationRequest.trainId.toString());

        // FIXME: Déplacer (infra)
        Map.Entry<String, List<TopologieDto.TopologieSeatDto>> wagonAvecPlace =
                findWagonAvecPlaces(reservationRequest, topologie);
        List<Seat> siegesDisponibles= getSiegesDisponibles(wagonAvecPlace);
        List<Seat> siegesReserves = selectSiegesDisponibles(reservationRequest, siegesDisponibles);

        if (!siegesReserves.isEmpty()) {
            Reservation reservation = new Reservation(
                    reservationRequest.trainId,
                    new BookingReference(bookingReferenceClient.generateBookingReference()),
                    siegesReserves
            );
            this.bookingReferenceClient.bookTrain(reservation.trainId.toString(), reservation.bookingReference.reference, reservation.seats);
            return Optional.of(reservation);
        } else {
            return Optional.empty();
        }
    }

    private List<Seat> getSiegesDisponibles(Map.Entry<String,
            List<TopologieDto.TopologieSeatDto>> wagonAvecPlace) {
        if(wagonAvecPlace==null){
            return Collections.emptyList();
        }
        return wagonAvecPlace.getValue()
                .stream()
                .filter(topologieSeatDto -> "".equals(topologieSeatDto.booking_reference))
                .map(topologieSeatDto -> new Seat(topologieSeatDto.coach, topologieSeatDto.seat_number)).collect(Collectors.toList());
    }

    private List<Seat> selectSiegesDisponibles(ReservationRequest reservationRequest, List<Seat> siegesDisponibles) {
        return siegesDisponibles
                .stream()
                .limit(reservationRequest.seatCount)
                .collect(Collectors.toList());
    }

    private Map.Entry<String, List<TopologieDto.TopologieSeatDto>> findWagonAvecPlaces(ReservationRequest reservationRequest, String topologie) {
        Map.Entry<String, List<TopologieDto.TopologieSeatDto>> wagonAvecPlace = null;
        Map<String, List<TopologieDto.TopologieSeatDto>> map = new HashMap<>();
        for (TopologieDto.TopologieSeatDto x : new Gson().fromJson(topologie, TopologieDto.class).seats.values()) {
            map.computeIfAbsent(x.coach, k -> new ArrayList<>()).add(x);
        }
        for (Map.Entry<String, List<TopologieDto.TopologieSeatDto>> wagon : map.entrySet()) {
            long siegesDisponibles = 0L;
            for (TopologieDto.TopologieSeatDto siege : wagon.getValue()) {
                if ("".equals(siege.booking_reference)) {
                    siegesDisponibles++;
                }
            }
            if (siegesDisponibles >= reservationRequest.seatCount) {
                wagonAvecPlace = wagon;
                break;
            }
        }
        return wagonAvecPlace;
    }
}