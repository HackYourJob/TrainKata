namespace TrainKata.Infra
{
    public interface ITrainDataClient
    {
        string GetTopology(string trainId);
    }
}