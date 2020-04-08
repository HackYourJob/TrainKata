using System.Collections.Generic;
using System.Linq;

namespace TrainKata.Domain
{
    public struct Topology
    {
        private readonly List<Coach> _coaches;

        public Topology(List<Coach> coaches)
        {
            _coaches = coaches;
        }

        public List<Seat> FindAvailableSeats(int seatCount)
        {
            return _coaches
                .Select(coach => coach.TryToFindAvailableSeats(seatCount))
                .FirstOrDefault(seats => seats.Any()) ?? new List<Seat>();
        }
    }
}