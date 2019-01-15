package infra;

import domain.in.TrainReservationFailure;
import domain.in.TrainReservationResult;
import domain.in.TrainReservationSuccess;

public class ReservationResponseSerializer {
    public String serialize(TrainReservationResult trainReservationResult) {
        if (trainReservationResult instanceof TrainReservationFailure) {
            return "{\"train_id\": \"" + ((TrainReservationFailure) trainReservationResult).trainId.trainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        } else if (trainReservationResult instanceof TrainReservationSuccess) {
            return "{" +
                    "\"train_id\": \"" + ((TrainReservationSuccess) trainReservationResult).trainId.trainId + "\", " +
                    "\"booking_reference\": \"" + ((TrainReservationSuccess) trainReservationResult).bookingId + "\", " +
                    "\"seats\": [" + ((TrainReservationSuccess) trainReservationResult).seats + "]" +
                    "}";
        }
        return null;
    }
}
