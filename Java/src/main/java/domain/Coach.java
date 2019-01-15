package domain;

import java.util.*;
import java.util.stream.Collectors;

public class Coach {

    private final String coachId;
    private Map<SeatId, SeatStatus> seats = new HashMap<>();

    public Coach(List<Topologie.TopologieSeat> seats, String coachId) {
        this.coachId = coachId;
        seats.forEach(seat -> this.seats.put(new SeatId(seat.seat_number), "".equals(seat.booking_reference) ? SeatStatus.ALREADY_BOOKED : SeatStatus.AVAILABLE));
    }

    public Optional<List<Seat>> tryToReserve(int seatCount) {
        List<SeatId> seatIds = seats.entrySet().stream().filter(es -> es.getValue() == SeatStatus.AVAILABLE).map(es -> new Seat(coachId, es.getKey())).limit(seatCount).collect(Collectors.toList());
        if (seatIds.size() == seatCount) {
            return Optional.of(seatIds);
        } else {
            return Optional.empty();
        }
    }
}
