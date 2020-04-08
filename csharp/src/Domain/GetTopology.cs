using TrainKata.Domain;

namespace TrainKata
{
    public interface GetTopology
    {
        Topology For(TrainId trainId);
    }
}