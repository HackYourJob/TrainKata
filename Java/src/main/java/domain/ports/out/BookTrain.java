package domain.ports.out;

import domain.Coach;
import domain.Reservation;
import domain.Seat;
import domain.TrainId;

import java.util.List;

public interface BookTrain {
    Reservation bookTrain(TrainId trainId, List<Seat> chosenSeats, Coach coach);
}
