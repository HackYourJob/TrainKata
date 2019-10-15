package domain;

import java.util.Optional;

public class MakeReservation {

    public Optional<Reservation> makeReservation(SeatCount seatCount, Topologie topologie) {
        var availableSeats = topologie.tryToGetAvailableSeats(seatCount);
        return Optional.empty();
    }
}
