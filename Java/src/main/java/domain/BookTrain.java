package domain;

import java.util.List;

public interface BookTrain {
    Reservation bookTrain(TrainId trainId, List<Seat> chosenSeats, Coach coach);
}
