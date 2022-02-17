namespace TrainKata.Domain;

public struct BookingReference
{
    public string Value { get; }

    public BookingReference(string value)
    {
        Value = value;
    }

    public override string ToString()
    {
        return Value;
    }
}