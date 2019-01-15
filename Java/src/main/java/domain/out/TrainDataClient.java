package domain.out;

import domain.Topologie;
import domain.in.TrainId;

public interface TrainDataClient {
    Topologie getTopology(TrainId trainId);
}
