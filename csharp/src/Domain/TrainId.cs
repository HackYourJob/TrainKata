namespace TrainKata.Domain;

public class TrainId
{
    private readonly string _id;

    public TrainId(string id)
    {
        _id = id;
    }

    public override string ToString()
    {
        return _id;
    }
}