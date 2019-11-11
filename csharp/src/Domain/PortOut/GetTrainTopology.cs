using TrainKata.Domain.Options;

namespace TrainKata.Domain.PortOut
{
    public interface GetTrainTopology
    {
        Maybe<Topology> ById(TrainId id);
    }
}