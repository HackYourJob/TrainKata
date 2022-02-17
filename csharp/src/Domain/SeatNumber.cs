namespace KataTrainReservation.Domain;

public struct SeatNumber
{
    public int Number { get; }

    public SeatNumber(int number)
    {
        Number = number;
    }
}