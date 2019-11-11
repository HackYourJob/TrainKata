namespace TrainKata.Infra.TrainDataClient
{
    public interface ITrainDataClient
    {
        string GetTopology(string trainId);
    }
}