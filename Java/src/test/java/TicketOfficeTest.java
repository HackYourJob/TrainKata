import org.junit.*;

import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TicketOfficeTest {
    
    @Test
    public void reserveSeats() {
        //GIVEN
        //TODO use real trainDataRepository
        TicketOffice ticketOffice = new TicketOffice(new SeatsOptimizer(null));
        ReservationRequest request = new ReservationRequest("1234", 5);

        //WHEN
        Reservation reservation = ticketOffice.makeReservation(request);

        //THEN
        assertEquals(reservation.trainId, request.trainId);
        assertEquals(reservation.seats.size(), request.seatCount);
        assertEquals(reservation.seats.stream()
                .collect(Collectors.groupingBy(s -> s.coach)).size(), 1);
        // TODO How to check 70%
    }
}
