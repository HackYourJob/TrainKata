package domain;

import java.util.List;

import infra.out.Seat;

public interface BookingReferenceClient {
    String generateBookingReference();

    void bookTrain(String trainId, String bookingReference, List<Seat> seats);
}
