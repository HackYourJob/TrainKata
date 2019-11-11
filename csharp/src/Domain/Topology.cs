using System.Collections.Generic;
using System.Linq;
using TrainKata.Domain.Options;

namespace TrainKata.Domain
{
    public struct Topology
    {
        public ICollection<Coach> Coaches { get; }

        public Topology(ICollection<Coach> coaches)
        {
            Coaches = coaches;
        }

        public Maybe<List<SeatId>> TryToReserveSeats(int seatsNumber)
        {
            return Coaches.Select(coach => coach.TryToReserveSeats(seatsNumber)).FirstOrDefault(seats => seats.HasValue()) ?? Maybe.None<List<SeatId>>();
        }
    }
}