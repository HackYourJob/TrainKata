import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketOfficeService {

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
    }

    public String makeReservation(ReservationRequestDto request) {
        ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, new ArrayList<>(), "");
        return serialize(reservation);
    }

    private String serialize(ReservationResponseDto reservation) {
        return "{" +
                "\"train_id\": \"" + reservation.trainId + "\", " +
                "\"booking_reference\": \"" + reservation.bookingId + "\", " +
                "\"seats\": [" + reservation.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";
    }
}