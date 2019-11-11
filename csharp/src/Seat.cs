namespace TrainKata
{
    public struct Seat
    {
        public SeatId Id { get; }

        public SeatStatus Status { get; }

        public Seat(SeatId id, SeatStatus status)
        {
            Id = id;
            Status = status;
        }

        public enum SeatStatus
        {
            Available,
            Reserved
        }

        public bool IsAvailable()
        {
            return Status == SeatStatus.Available;
        }
    }
}