namespace TrainKata.Domain
{
    public struct Seat
    {
        public bool IsAvailable { get; }
        public string CoachId { get; }
        public int SeatNumber { get; }

        public Seat(string bookingReference, string coachId, int seatNumber)
        {
            CoachId = coachId;
            SeatNumber = seatNumber;
            IsAvailable = string.IsNullOrEmpty(bookingReference);
        }
    }
}