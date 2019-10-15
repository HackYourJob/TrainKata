package domain;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Coach {

    public final CoachId id;
    public final List<CoachSeat> seats;

    public Coach(CoachId id, List<CoachSeat> seats) {
        this.id = id;
        this.seats = seats;
    }

    public List<Seat> getAvailableSeats() {

        return seats.stream()
                .filter(seat -> SeatAvailability.AVAILABLE.equals(seat.availability))
                .map(coachSeat -> coachSeat.seat)
                .collect(Collectors.toList());
    }

    public Optional<List<Seat>> tryToGetAvailableSeats(SeatCount seatCount) {
        return Optional.of(
                getAvailableSeats()
                .stream()
                .limit(seatCount.count)
                .collect(Collectors.toList())
        ).filter(availableSeats -> availableSeats.size() >= seatCount.count);
    }
}
