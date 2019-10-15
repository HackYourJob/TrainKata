package infra.booking_reference_client;

import domain.BookingReference;
import domain.Reservation;
import domain.out.BookTrain;
import domain.out.GenerateBookingReference;
import infra.BookingReferenceClient;

public class BookingReferenceAdapter implements BookTrain, GenerateBookingReference {

    private final BookingReferenceClient bookingReferenceClient;

    public BookingReferenceAdapter(BookingReferenceClient bookingReferenceClient) {
        this.bookingReferenceClient = bookingReferenceClient;
    }

    @Override
    public void execute(Reservation reservation) {
        this.bookingReferenceClient.bookTrain(reservation.trainId.toString(), reservation.bookingReference.reference, reservation.seats);
    }

    @Override
    public BookingReference execute() {
        return new BookingReference(bookingReferenceClient.generateBookingReference());
    }
}
