using System.Collections.Generic;
using System.Linq;
using TrainKata.Domain.Options;

namespace TrainKata.Domain
{
    public struct Coach
    {
        public ICollection<Seat> Seats { get; }

        public Coach(ICollection<Seat> seats)
        {
            Seats = seats;
        }

        public Maybe<List<SeatId>> TryToReserveSeats(int seatsNumber)
        {
            var availableSeats = Seats.Where(seat => seat.IsAvailable()).Select(seat => seat.Id).ToArray();
            return availableSeats.Length >= seatsNumber
                ? Maybe.Some(availableSeats.Take(seatsNumber).ToList())
                : Maybe.None<List<SeatId>>();
        }
    }
}