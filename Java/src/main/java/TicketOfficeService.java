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

    public String makeReservation(ReservationRequestDto request) {
        return  makeReservation(request.toDomainModel());
    }

    // FIXME : Trop grosse !
    // FIXME : Retourner une réservation
    public String makeReservation(ReservationRequest reservationRequest) {
        // FIXME : Retourner une topologie
        String topologie = trainDataClient.getTopology(reservationRequest.trainId);

        // FIXME: Déplacer (infra)
        Map.Entry<String, List<Topologie.TopologieSeat>> wagonAvecPlace =
                findWagonAvecPlaces(reservationRequest, topologie);
        List<Seat> siegesReserves = selectSiegesDisponibles(reservationRequest, wagonAvecPlace);

        if (!siegesReserves.isEmpty()) {
            ReservationResponseDto reservation = new ReservationResponseDto(reservationRequest.trainId, siegesReserves, bookingReferenceClient.generateBookingReference());
            this.bookingReferenceClient.bookTrain(reservation.trainId, reservation.bookingId, reservation.seats);
            return "{" +
                    "\"train_id\": \"" + reservation.trainId + "\", " +
                    "\"booking_reference\": \"" + reservation.bookingId + "\", " +
                    "\"seats\": [" + reservation.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                    "}";
        } else {
            return "{\"train_id\": \"" + reservationRequest.trainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }
    }

    private List<Seat> selectSiegesDisponibles(ReservationRequest reservationRequest, Map.Entry<String, List<Topologie.TopologieSeat>> wagonAvecPlace) {
        List<Seat> siegesReserves = new ArrayList<>();
        if(wagonAvecPlace != null) {
            long limit = reservationRequest.seatCount;
            for (Topologie.TopologieSeat siege : wagonAvecPlace.getValue()) {
                if ("".equals(siege.booking_reference)) {
                    if (limit-- == 0) break;
                    Seat seat = new Seat(siege.coach, siege.seat_number);
                    siegesReserves.add(seat);
                }
            }
        }
        return siegesReserves;
    }

    private Map.Entry<String, List<Topologie.TopologieSeat>> findWagonAvecPlaces(ReservationRequest reservationRequest, String topologie) {
        Map.Entry<String, List<Topologie.TopologieSeat>> wagonAvecPlace = null;
        Map<String, List<Topologie.TopologieSeat>> map = new HashMap<>();
        for (Topologie.TopologieSeat x : new Gson().fromJson(topologie, Topologie.class).seats.values()) {
            map.computeIfAbsent(x.coach, k -> new ArrayList<>()).add(x);
        }
        for (Map.Entry<String, List<Topologie.TopologieSeat>> wagon : map.entrySet()) {
            long siegesDisponibles = 0L;
            for (Topologie.TopologieSeat siege : wagon.getValue()) {
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