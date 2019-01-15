package domain;

import domain.in.TrainId;

public class TrainReservationFailure implements TrainReservationResult {
    public final TrainId trainId;

    public TrainReservationFailure(TrainId trainId) {
        this.trainId = trainId;
    }
}
