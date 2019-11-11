namespace TrainKata
{
    public struct SeatId
    {
        public int Number { get; }

        public string CoachId { get; }

        public SeatId(int number, string coachId)
        {
            Number = number;
            CoachId = coachId;
        }

        public override string ToString()
        {
            return Number + CoachId;
        }
    }
}