namespace TrainKata.Domain
{
    public struct SeatId
    {
        public string CoachId { get; }

        public int SeatNumber { get; }

        public SeatId(string coachId, int seatNumber)
        {
            CoachId = coachId;
            SeatNumber = seatNumber;
        }

        public override string ToString()
        {
            return CoachId + SeatNumber;
        }
    }
}