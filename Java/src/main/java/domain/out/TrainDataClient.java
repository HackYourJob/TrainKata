package domain.out;

import domain.in.TrainId;

public interface TrainDataClient {
    String getTopology(TrainId trainId);
    //Topology getTopology(TrainId trainId);
}
