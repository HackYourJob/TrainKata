package domain;

import java.util.List;

public interface TrainDataClient {
    List<Seat> getTopology(String trainId);
}
