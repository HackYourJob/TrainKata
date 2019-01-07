import org.junit.*;

import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TicketOfficeServiceTest {
    
    @Test
    public void reserveSeats() {
        //GIVEN
        //TODO use real trainDataRepository
        TicketOfficeService ticketOffice = new TicketOfficeService();
        ReservationRequestDto request = new ReservationRequestDto("1234", 5);

        //WHEN
        ReservationResponseDto reservation = ticketOffice.makeReservation(request);

        //THEN
        assertEquals(reservation.trainId, request.trainId);
        assertEquals(request.seatCount, reservation.seats.size());
        assertEquals(reservation.seats.stream()
                .collect(Collectors.groupingBy(s -> s.coach)).size(), 1);
        // TODO How to check 70%
    }
}
