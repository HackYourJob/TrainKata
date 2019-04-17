package domain.ports.out;

import domain.Coach;
import domain.ReservationSucceed;
import domain.Seat;
import domain.TrainId;

import java.util.List;

public interface BookTrain {
    ReservationSucceed bookTrain(TrainId trainId, List<Seat> chosenSeats, Coach coach);
}
