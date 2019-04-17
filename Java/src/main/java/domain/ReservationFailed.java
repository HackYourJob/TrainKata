package domain;

public class ReservationFailed implements ReservationDomainEvent{
    public final TrainId trainId;

    public ReservationFailed(TrainId trainId) {
        this.trainId = trainId;
    }
}
