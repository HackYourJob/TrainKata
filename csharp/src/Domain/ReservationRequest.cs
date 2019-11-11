namespace TrainKata.Domain
{
    public struct ReservationRequest
    {
        public TrainId TrainId { get; }

        public int SeatsNumber { get; }

        public ReservationRequest(TrainId trainId, int seatsNumber)
        {
            TrainId = trainId;
            SeatsNumber = seatsNumber;
        }
    }
}