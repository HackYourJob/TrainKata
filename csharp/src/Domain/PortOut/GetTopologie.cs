using System.Collections.Generic;

namespace TrainKata.Domain.PortOut;

public interface GetTopologie
{
    List<Coach> OfTrain(TrainId id);
}