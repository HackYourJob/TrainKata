using System.Collections.Generic;
using System.Linq;
using TrainKata.Domain;

namespace TrainKata
{
    public class MakeReservation
    {
        private readonly GetTopology _getTopology;
        private readonly BookReservation _bookReservation;

        public MakeReservation(GetTopology getTopology, BookReservation bookReservation)
        {
            _getTopology = getTopology;
            _bookReservation = bookReservation;
        }

        public Reservation? Execute(ReservationRequest reservationRequest)
        {
            var topology = GetTopology(reservationRequest);

            var availableSeats = TryToFindAvailableSeats(reservationRequest, topology);

            return Book(reservationRequest.TrainId, availableSeats);
        }

        private Topology GetTopology(ReservationRequest reservationRequest)
        {
            return _getTopology.For(reservationRequest.TrainId);
        }

        private List<Seat> TryToFindAvailableSeats(ReservationRequest request, Topology topology)
        {
            return topology.FindAvailableSeats(request.SeatCount);
        }

        private Reservation? Book(TrainId trainId, List<Seat> availableSeats)
        {
            if (availableSeats.Any())
            {
                return _bookReservation.Book(trainId, availableSeats);
            }

            return null;
        }
    }
}