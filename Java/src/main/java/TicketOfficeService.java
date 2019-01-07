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
        String data = trainDataClient.getTopology(request.trainId);

        Map<String, List<Topologie.TopologieSeat>> map = new HashMap<>();
        for (Topologie.TopologieSeat x : new Gson().fromJson(data, Topologie.class).seats.values()) {
            map.computeIfAbsent(x.coach, k -> new ArrayList<>()).add(x);
        }
        Map.Entry<String, List<Topologie.TopologieSeat>> found = null;
        for (Map.Entry<String, List<Topologie.TopologieSeat>> x : map.entrySet()) {
            long count = 0L;
            for (Topologie.TopologieSeat y : x.getValue()) {
                if ("".equals(y.booking_reference)) {
                    count++;
                }
            }
            if (count >= request.seatCount) {
                found = x;
                break;
            }
        }
        List<Seat> seats = new ArrayList<>();
        if(found != null) {
            List<Seat> list = new ArrayList<>();
            long limit = request.seatCount;
            for (Topologie.TopologieSeat y : found.getValue()) {
                if ("".equals(y.booking_reference)) {
                    if (limit-- == 0) break;
                    Seat seat = new Seat(y.coach, y.seat_number);
                    list.add(seat);
                }
            }
            seats = list;
        }

        if (!seats.isEmpty()) {
            ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, seats, bookingReferenceClient.generateBookingReference());
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