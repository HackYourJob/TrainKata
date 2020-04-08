using System.Collections.Generic;
using System.Linq;

namespace TrainKata.Domain
{
    public struct Coach
    {
        public List<Seat> Seats { get; }

        public Coach(List<Seat> seats)
        {
            Seats = seats;
        }

        public List<Seat> FindAvailableSeats(int requestSeatCount)
        {
            return Seats
                .Where(seat => seat.IsAvailable)
                .Take(requestSeatCount)
                .ToList();
        }
    }
}