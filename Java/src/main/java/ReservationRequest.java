public class ReservationRequest {

    public final TrainId trainId;
    public final SeatCount seatCount;

    public ReservationRequest(TrainId trainId, SeatCount seatCount) {
        this.trainId = trainId;
        this.seatCount = seatCount;
    }

}
