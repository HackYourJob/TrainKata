import java.util.ArrayList;
import java.util.List;

public class TicketOfficeService {

    public TicketOfficeService() {
    }

    public ReservationResponseDto makeReservation(ReservationRequestDto request) {
        ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, new ArrayList<>(), "");
        return reservation;
    }

}