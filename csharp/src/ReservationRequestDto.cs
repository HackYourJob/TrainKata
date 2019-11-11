namespace TrainKata
{
    public class ReservationRequestDto
    {
        public string TrainId { get; }
        public int SeatCount { get; }

        public ReservationRequestDto(string trainId, int seatCount)
        {
            TrainId = trainId;
            SeatCount = seatCount;
        }

        public ReservationRequest ToRequest()
        {
            return new ReservationRequest(new TrainId(TrainId), SeatCount);
        }
    }
}