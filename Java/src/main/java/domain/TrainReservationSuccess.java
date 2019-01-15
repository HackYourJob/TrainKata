package domain;

import domain.in.TrainId;

import java.util.List;
import java.util.stream.Collectors;

public class TrainReservationSuccess implements TrainReservationResult {
    public final TrainId trainId;
    public final String bookingId;
    public final String seats;

    public TrainReservationSuccess(TrainId trainId, String bookingId, List<Seat> seats) {
        this.trainId = trainId;
        this.bookingId = bookingId;
        this.seats = seats.stream()
                .map(s -> "\"" + s.seatNumber + s.coach + "\"")
                .collect(Collectors.joining(", "));
    }
}
