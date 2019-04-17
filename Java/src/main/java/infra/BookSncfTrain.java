package infra;

import domain.*;
import domain.ports.out.BookTrain;

import java.util.List;
import java.util.stream.Collectors;

public class BookSncfTrain implements BookTrain {

    private BookingReferenceClient bookingReferenceClient;

    public BookSncfTrain(BookingReferenceClient bookingReferenceClient) {
        this.bookingReferenceClient = bookingReferenceClient;
    }


    @Override
    public ReservationSucceed bookTrain(TrainId trainId, List<Seat> chosenSeats, Coach coach) {
        ReservationSucceed reservation = new ReservationSucceed(
                trainId,
                bookingReferenceClient.generateBookingReference(),
                coach.id,
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
