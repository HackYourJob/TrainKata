package domain;

import infra.ReservationRequestDto;

import java.util.List;

public interface GetTrainTopology {

    List<Coach> getTrainTopology(TrainId trainId);
}
