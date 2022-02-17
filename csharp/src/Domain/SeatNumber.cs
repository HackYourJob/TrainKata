namespace TrainKata.Domain;

public struct SeatNumber
{
    public int Number { get; }

    public SeatNumber(int number)
    {
        Number = number;
    }

    public override string ToString()
    {
        return Number.ToString();
    }
}