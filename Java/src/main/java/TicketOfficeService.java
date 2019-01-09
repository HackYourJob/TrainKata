import java.util.*;

public class TicketOfficeService {

    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    public ReservationResponseDto makeReservation(ReservationRequestDto request) {
        Topologie topologie = trainDataClient.getTopology(request.trainId);

        Map<String, List<Topologie.TopologieSeat>> map = new HashMap<>();
        for (Topologie.TopologieSeat x : topologie.seats.values()) {
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
        if (found != null) {
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

        if (seats.isEmpty()) {
            return new ReservationResponseDto(request.trainId, new ArrayList<>(), "");
        }
        String bookingId = bookingReferenceClient.generateBookingReference();
        return new ReservationResponseDto(request.trainId, seats, bookingId);
    }
}