namespace KataTrainReservation.Domain;

public struct CoachId
{
    public string Value { get; }

    public CoachId(string value)
    {
        Value = value;
    }
}