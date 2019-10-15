package domain.in;

import domain.*;
import domain.out.BookTrain;
import domain.out.GenerateBookingReference;
import domain.out.GetTopologie;

import java.util.List;
import java.util.Optional;

public class MakeReservation {

    private final GetTopologie getTopologie;
    private final BookTrain bookTrain;
    private final GenerateBookingReference generateBookingReference;

    public MakeReservation(GetTopologie getTopologie, BookTrain bookTrain, GenerateBookingReference generateBookingReference) {
        this.getTopologie = getTopologie;
        this.bookTrain = bookTrain;
        this.generateBookingReference = generateBookingReference;
    }

    public Optional<Reservation> execute(ReservationRequest reservationRequest) {

        return getTopologie.getByTrainId(reservationRequest.trainId)
                .tryToGetAvailableSeats(reservationRequest.seatCount)
                .map(seats -> confirmReservation(reservationRequest,seats));
    }

    private Reservation confirmReservation(ReservationRequest reservationRequest, List<Seat> availableSeats) {
        BookingReference bookingReference = generateBookingReference.execute();

        Reservation reservation = new Reservation(
                reservationRequest.trainId,
                bookingReference,
                availableSeats
        );

        bookTrain.execute(reservation);
        return reservation;
    }
}
