using System.Collections.Generic;
using System.Linq;

namespace KataTrainReservation
{
    public class MakeReservation
    {
        private readonly BookingTrain _bookingTrain;
        private readonly GetTopologie _getTopologie;

        public MakeReservation(GetTopologie getTopologie, BookingTrain bookingTrain)
        {
            _getTopologie = getTopologie;
            _bookingTrain = bookingTrain;
        }

        public Reservation? Execute(ReservationRequest reservationRequest)
        {
            var topologie = _getTopologie.GetTopologie(reservationRequest.TrainId);
            
            var firstAvailableCoach = GetFirstAvailableCoach(reservationRequest, topologie);
            var seats = SelectSeatsToBook(reservationRequest, firstAvailableCoach).ToList();
            return BookSeats(reservationRequest, seats);
        }

        private Reservation? BookSeats(ReservationRequest request, List<Seat> seats)
        {
            if (seats.Count == 0)
            {
                return default;
            }

            var bookingReference = _bookingTrain.Book(request.TrainId, seats.Select(seat=>seat.Id).ToList());
            return new Reservation(request.TrainId, bookingReference, seats.Select(seat=>seat.Id));
        }

        private static IEnumerable<Seat> SelectSeatsToBook(ReservationRequest request, Coach? firstAvailableCoach)
        {
           return (firstAvailableCoach?.Seats ?? new List<Seat>()) 
                .Where(seat => seat.IsAvailable)
                .Take(request.SeatCount);
        }

        private static Coach? GetFirstAvailableCoach(
            ReservationRequest request, 
            Topologie topologie)
        {
            return topologie.Coaches
                .FirstOrDefault(coach =>
                {
                    var seats = coach.Seats;
                    return seats.Count(seat => seat.IsAvailable) >= request.SeatCount;
                });
        }
    }
}
