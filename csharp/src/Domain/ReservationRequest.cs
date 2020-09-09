namespace KataTrainReservation
{
    public struct ReservationRequest
    {
        public TrainId TrainId { get; private set; }
        public int SeatCount { get; private set; }

        public ReservationRequest(TrainId trainId, int seatCount)
        {
            this.TrainId = trainId;
            this.SeatCount = seatCount;
        }
    }

    public struct TrainId
    {
        public TrainId(string id)
        {
            Id = id;
        }

        public string Id { get; }
    }
}