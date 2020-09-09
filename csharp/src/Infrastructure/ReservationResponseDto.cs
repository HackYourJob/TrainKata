using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace KataTrainReservation
{
    public class ReservationResponseDto
    {
        public string TrainId { get; private set; }
        public string BookingId { get; private set; }
        public List<Seat> Seats { get; private set; }

        private ReservationResponseDto(string trainId, List<Seat> seats, string bookingReference)
        {
            this.TrainId = trainId;
            this.BookingId = bookingReference;
            this.Seats = seats;
        }

        public static ReservationResponseDto Success(string trainId, List<Seat> seats, string bookingReference)
        {
            return new ReservationResponseDto(trainId, seats, bookingReference);
        }

        public static ReservationResponseDto Failed(string trainId)
        {
            return new ReservationResponseDto(trainId, new List<Seat>(), String.Empty);
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
