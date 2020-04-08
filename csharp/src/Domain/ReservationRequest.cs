using TrainKata.Domain;

namespace TrainKata
{
    public struct ReservationRequest
    {
        public TrainId TrainId { get; }
        public int SeatCount { get; }

        public ReservationRequest(TrainId trainId, int seatCount)
        {
            TrainId = trainId;
            SeatCount = seatCount;
        }
    }
}