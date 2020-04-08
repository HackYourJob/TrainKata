using System.Collections.Generic;
using System.Linq;

namespace TrainKata.Domain
{
    public struct Coach
    {
        private readonly List<Seat> _seats;

        public Coach(List<Seat> seats)
        {
            _seats = seats;
        }

        public List<Seat> TryToFindAvailableSeats(int requestSeatCount)
        {
            if (!IsAvailable(requestSeatCount))
            {
                return new List<Seat>();
            }

            return _seats
                .Where(seat => seat.IsAvailable)
                .Take(requestSeatCount)
                .ToList();
        }

        private bool IsAvailable(int seatCount)
        {
            return _seats.Count(s => s.IsAvailable) >= seatCount;
        }
    }
}