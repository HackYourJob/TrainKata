package domain.train;

import java.util.*;
import java.util.stream.Collectors;

public class Coach {

    // TODO: 15/01/19 Wrap Primitive into CoachId
    private final String coachId;
    private Map<SeatId, SeatStatus> seats = new HashMap<>();

    public Coach(List<Topologie.TopologieSeat> seats, String coachId) {
        this.coachId = coachId;
        seats.forEach(seat -> this.seats.put(new SeatId(seat.seat_number), "".equals(seat.booking_reference) ? SeatStatus.AVAILABLE : SeatStatus.ALREADY_BOOKED));
    }

    public Optional<List<Seat>> tryToReserve(int seatCount) {

        List<Seat> seat = seats.entrySet().stream()
                .filter(es -> es.getValue() == SeatStatus.AVAILABLE)
                .map(es -> new Seat(coachId, es.getKey().seat_number))
                .sorted(Comparator.comparing(Seat::sorter))
                .limit(seatCount)
                .collect(Collectors.toList());

        if (seat.size() == seatCount) {
            return Optional.of(seat);
        } else {
            return Optional.empty();
        }
    }

    public enum SeatStatus {
        AVAILABLE,
        ALREADY_BOOKED;

    }
}
