package domain.out;

import domain.Topologie;
import domain.TrainId;

public interface GetTopologie {

    Topologie getByTrainId(TrainId trainId);

}
