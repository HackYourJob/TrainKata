package infra;

import domain.*;

import java.util.List;
import java.util.stream.Collectors;

public class BookSncfTrain implements BookTrain {

    private BookingReferenceClient bookingReferenceClient;

    public BookSncfTrain(BookingReferenceClient bookingReferenceClient) {
        this.bookingReferenceClient = bookingReferenceClient;
    }


    @Override
    public Reservation bookTrain(TrainId trainId, List<Seat> chosenSeats, Coach coach) {
        Reservation reservation = new Reservation(
                trainId,
                bookingReferenceClient.generateBookingReference(),
                chosenSeats
        );
        bookingReferenceClient.bookTrain(
                reservation.trainId.id,
                reservation.bookingId,
                reservation.seats.stream().map(s -> new SeatDto(coach.id, s.id)).collect(Collectors.toList())
        );
        return reservation;
    }

}
