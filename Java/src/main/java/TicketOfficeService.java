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

    // FIXME : Trop grosse !
    // FIXME : Retourner une réservation
    public String makeReservation(ReservationRequestDto request) {
        // FIXME : Retourner une topologie
        String data = trainDataClient.getTopology(request.trainId);

        // FIXME: Déplacer (infra)
        Map<String, List<Topologie.TopologieSeat>> map = new HashMap<>();
        for (Topologie.TopologieSeat x : new Gson().fromJson(data, Topologie.class).seats.values()) {
            map.computeIfAbsent(x.coach, k -> new ArrayList<>()).add(x);
        }
        Map.Entry<String, List<Topologie.TopologieSeat>> wagonAvecPlace = null;
        for (Map.Entry<String, List<Topologie.TopologieSeat>> wagon : map.entrySet()) {
            long siegesDisponibles = 0L;
            for (Topologie.TopologieSeat siege : wagon.getValue()) {
                if ("".equals(siege.booking_reference)) {
                    siegesDisponibles++;
                }
            }
            if (siegesDisponibles >= request.seatCount) {
                wagonAvecPlace = wagon;
                break;
            }
        }
        List<Seat> siegesReserves = new ArrayList<>();
        if(wagonAvecPlace != null) {
            long limit = request.seatCount;
            for (Topologie.TopologieSeat siege : wagonAvecPlace.getValue()) {
                if ("".equals(siege.booking_reference)) {
                    if (limit-- == 0) break;
                    Seat seat = new Seat(siege.coach, siege.seat_number);
                    siegesReserves.add(seat);
                }
            }
        }

        if (!siegesReserves.isEmpty()) {
            ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, siegesReserves, bookingReferenceClient.generateBookingReference());
            this.bookingReferenceClient.bookTrain(reservation.trainId, reservation.bookingId, reservation.seats);
            return "{" +
                    "\"train_id\": \"" + reservation.trainId + "\", " +
                    "\"booking_reference\": \"" + reservation.bookingId + "\", " +
                    "\"seats\": [" + reservation.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                    "}";
        } else {
            return "{\"train_id\": \"" + request.trainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }
    }
}