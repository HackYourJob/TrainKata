namespace TrainKata.Domain
{
    public struct Seat
    {
        public bool IsAvailable { get; }

        public SeatId Id { get; }

        public Seat(SeatId id, bool isAvailable)
        {
            Id = id;
            IsAvailable = isAvailable;
        }
    }
}