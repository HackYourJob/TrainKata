package domain;

import infra.out.Topologie;

public interface TrainDataClient {
    Topologie getTopology(String trainId);
}
