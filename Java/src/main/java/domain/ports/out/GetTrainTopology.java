package domain.ports.out;

import domain.Coach;
import domain.TrainId;
import infra.ReservationRequestDto;

import java.util.List;

public interface GetTrainTopology {

    List<Coach> getTrainTopology(TrainId trainId);
}
