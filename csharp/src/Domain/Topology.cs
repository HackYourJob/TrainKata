using System.Collections.Generic;
using System.Linq;

namespace TrainKata.Domain
{
    public struct Topology
    {
        public List<Coach> Coaches { get; }

        public Topology(List<Coach> coaches)
        {
            Coaches = coaches;
        }

        public List<Seat> FindAvailableSeats(int seatCount)
        {
            return Coaches
                .Where(coach => coach.Seats.Count(s => s.IsAvailable) >= seatCount)
                .Select(c => c.FindAvailableSeats(seatCount))
                .FirstOrDefault() ?? new List<Seat>();
        }
    }
}