using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace KataTrainReservation
{
    public class ReservationResponseDto
    {
        public string TrainId { get; private set; }
        public string BookingId { get; private set; }
        public List<SeatDto> Seats { get; private set; }

        private ReservationResponseDto(string trainId, List<SeatDto> seats, string bookingReference)
        {
            this.TrainId = trainId;
            this.BookingId = bookingReference;
            this.Seats = seats;
        }

        public static ReservationResponseDto Success(TrainId trainId, List<Seat> seats, string bookingReference)
        {
            return new ReservationResponseDto(trainId.Id, seats.Select(seat => new SeatDto(seat.Id.CoachNumber, seat.Id.SeatNumber)).ToList(), bookingReference);
        }

        public static ReservationResponseDto Failed(TrainId trainId)
        {
            return new ReservationResponseDto(trainId.Id, new List<SeatDto>(), String.Empty);
        }

        public static ReservationResponseDto FromReservation(ReservationRequest request, Reservation? reservation)
        {
            if (reservation.HasValue)
                return new ReservationResponseDto(reservation.Value.TrainId.Id,
                    reservation.Value.Seats.Select(seat => new SeatDto(seat.CoachNumber, seat.SeatNumber)).ToList(),
                    reservation.Value.BookingReference.Reference);
            return Failed(request.TrainId);
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
