namespace TrainKata.Domain
{
    public struct SeatId
    {
        public CoachId CoachId { get; }
        public SeatNumber SeatNumber { get; }

        public SeatId(CoachId coachId, SeatNumber seatNumber)
        {
            CoachId = coachId;
            SeatNumber = seatNumber;
        }

        public override string ToString()
        {
            return $"{SeatNumber}{CoachId}";
        }
    }
}