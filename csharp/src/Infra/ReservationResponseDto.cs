using System.Collections.Generic;

namespace TrainKata.Infra
{
    public class ReservationResponseDto
    {
        public string TrainId { get; }
        public string BookingId { get; }
        public List<SeatDto> Seats { get; }

        public ReservationResponseDto(string trainId, List<SeatDto> seats, string bookingId)
        {
            TrainId = trainId;
            BookingId = bookingId;
            Seats = seats;
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
