package domain.in;

import domain.BookingReference;
import domain.Reservation;
import domain.ReservationRequest;
import domain.Topologie;
import domain.out.BookTrain;
import domain.out.GenerateBookingReference;
import domain.out.GetTopologie;

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
        // FIXME : Retourner une topologie
        // FIXME: Déplacer (infra)
        Topologie topologie = getTopologie.getByTrainId(reservationRequest.trainId);

        var availableSeats = topologie.tryToGetAvailableSeats(reservationRequest.seatCount);
        if (availableSeats.isEmpty()) {
            return Optional.empty();
        }
        BookingReference bookingReference = generateBookingReference.execute();

        Reservation reservation = new Reservation(
                reservationRequest.trainId,
                bookingReference,
                availableSeats.get()
        );

        bookTrain.execute(reservation);
        return Optional.of(reservation);
    }
}
