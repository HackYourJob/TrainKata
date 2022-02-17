namespace TrainKata.Domain;

public struct CoachId
{
    public string Value { get; }

    public CoachId(string value)
    {
        Value = value;
    }

    public override string ToString()
    {
        return Value;
    }
}