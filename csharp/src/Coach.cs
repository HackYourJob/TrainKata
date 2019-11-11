using System.Collections.Generic;

namespace TrainKata
{
    public struct Coach
    {
        public ICollection<Seat> Seats { get; }

        public Coach(ICollection<Seat> seats)
        {
            Seats = seats;
        }
    }
}