using System.Collections.Generic;
using System.Linq;
using TrainKata.Domain;

namespace TrainKata.Infra
{
    public class ReservationResponseDto
    {
        public string TrainId { get; private set; }
        public string BookingId { get; private set; }
        public List<string> Seats { get; private set; }

        public ReservationResponseDto(string trainId, List<string> seats, string bookingId)
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

        public static ReservationResponseDto Succeeded(Reservation reservation)
        {
            return new ReservationResponseDto(reservation.TrainId.Value, reservation.Seats.Select(s => s.ToString()).ToList(), reservation.BookingId);
        }

        public static ReservationResponseDto Refused(TrainId trainId)
        {
            return new ReservationResponseDto(trainId.Value, new List<string>(), "");
        }
    }
}
