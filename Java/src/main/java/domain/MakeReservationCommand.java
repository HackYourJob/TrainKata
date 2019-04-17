package domain;

public class MakeReservationCommand {
    public final TrainId trainId;
    public final int nbSeats;

    public MakeReservationCommand(TrainId trainId, int nbSeats) {
        this.trainId = trainId;
        this.nbSeats = nbSeats;
    }
}
