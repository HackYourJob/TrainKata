package domain;

public class MakeReservation {
    public final TrainId trainId;
    public final int nbSeats;

    public MakeReservation(TrainId trainId, int nbSeats) {
        this.trainId = trainId;
        this.nbSeats = nbSeats;
    }
}
