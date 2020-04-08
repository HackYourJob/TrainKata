using TrainKata.Domain;

namespace TrainKata.Infra
{
    public class ReservationRequestDto
    {
        public string TrainId { get; private set; }
        public int SeatCount { get; private set; }

        public ReservationRequestDto(string trainId, int seatCount)
        {
            this.TrainId = trainId;
            this.SeatCount = seatCount;
        }

        public ReservationRequest ToDomain()
        {
            return new ReservationRequest(new TrainId(TrainId), SeatCount);
        }
    }
}