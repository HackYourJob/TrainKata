using System.Collections.Generic;

namespace TrainKata.Infra
{
    public class ReservationResponseDto
    {
        public string TrainId { get; private set; }
        public string BookingId { get; private set; }
        public List<SeatDto> Seats { get; private set; }

        public ReservationResponseDto(string trainId, List<SeatDto> seats, string bookingId)
        {
            this.TrainId = trainId;
            this.BookingId = bookingId;
            this.Seats = seats;
        }

        public override string ToString()
        {
            return "ReservationResponseDto{" +
                   "trainId='" + TrainId + '\'' +
                   ", bookingId='" + BookingId + '\'' +
                   ", seats=" + Seats +
                   '}';
        }
    }
}
