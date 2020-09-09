using System.Collections.Generic;

namespace KataTrainReservation
{

        public struct Coach
        {
            public IEnumerable<Seat> Seats { get; }

            public Coach(IList<Seat> seats)
            {
                Seats = seats;
            }
        }
    }