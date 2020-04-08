using System.Collections.Generic;
using TrainKata.Domain;

namespace TrainKata
{
    public class MakeReservation
    {
        private readonly GetTopology _getTopology;

        public MakeReservation(GetTopology getTopology)
        {
            _getTopology = getTopology;
        }

        public void Execute(ReservationRequest reservationRequest)
        {
            var topology = GetTopology(reservationRequest);
            // get topology
            // find available seats
            // book
        }

        private Topology GetTopology(ReservationRequest reservationRequest)
        {
            return _getTopology.For(reservationRequest.TrainId);
        }

        public List<Seat> TryToFindAvailableSeats(ReservationRequest request, Topology topology)
        {
            return topology.FindAvailableSeats(request.SeatCount);
        }
    }
}