package domain.in;

import domain.*;
import domain.out.BookingReferenceClient;
import domain.out.TrainDataClient;

import java.util.List;
import java.util.Optional;

public class MakeReservation {
    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public MakeReservation(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    public TrainReservationResult execute(TrainId trainId, int nbSeats) {
        Topologie topology = trainDataClient.getTopology(trainId);

        Train train = new Train(topology);

        Optional<List<Seat>> availableSeats = train.findAvailableSeats(nbSeats);

        return availableSeats
                .map(seats -> (TrainReservationResult) new TrainReservationSuccess(trainId, bookingReferenceClient.generateBookingReference(), seats))
                .orElse(new TrainReservationFailure(trainId));
    }
}
