using System.Collections.Generic;

namespace TrainKata
{
    public class ReservationResponseDto
    {
        public string TrainId { get; }
        public string BookingId { get; }
        public List<Seat> Seats { get; }

        public ReservationResponseDto(string trainId, List<Seat> seats, string bookingId)
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
