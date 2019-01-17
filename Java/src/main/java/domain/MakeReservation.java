package domain;

import java.util.List;

public class MakeReservation {
    private TrainDataClient trainDataClient;

    public MakeReservation(TrainDataClient trainDataClient) {
        this.trainDataClient = trainDataClient;
    }

    public Reservation execute(String trainId, int seatCount){
        List<Seat> seats = trainDataClient.getTopology(trainId);
//
//        Train train = new Train(topology);

        return null;
    }
}
