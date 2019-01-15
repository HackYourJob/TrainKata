package domain.in;

import domain.*;
import domain.out.BookingReferenceClient;
import domain.out.TrainDataClient;

import java.util.List;

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

        List<Seat> availableSeats = train.findAvailableSeats(nbSeats);

        if (availableSeats.isEmpty()) {
            return new TrainReservationFailure(trainId);
        }

        return new TrainReservationSuccess(trainId, bookingReferenceClient.generateBookingReference(), availableSeats);
    }
}
