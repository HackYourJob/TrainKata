package infra;

import domain.TrainReservationFailure;
import domain.TrainReservationResult;
import domain.TrainReservationSuccess;

import java.util.stream.Collectors;

public class ReservationResponseSerializer {
    public String serialize(TrainReservationResult trainReservationResult) {
        if (trainReservationResult instanceof TrainReservationFailure) {
            return "{\"train_id\": \"" + ( (TrainReservationFailure) trainReservationResult).trainId.trainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        } else if (trainReservationResult instanceof TrainReservationSuccess) {
            return "{" +
                    "\"train_id\": \"" + ((TrainReservationSuccess)trainReservationResult).trainId.trainId + "\", " +
                    "\"booking_reference\": \"" + ((TrainReservationSuccess)trainReservationResult).bookingId + "\", " +
                    "\"seats\": [" + ((TrainReservationSuccess)trainReservationResult).seats + "]" +
                    "}";
        }
        return null;
    }
}
