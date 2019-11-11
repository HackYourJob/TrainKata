using System.Collections.Generic;
using System.Linq;
using TrainKata.Domain.Options;
using TrainKata.Domain.PortOut;

namespace TrainKata.Domain.PortIn
{
    public class MakeReservation
    {
        private readonly GetTrainTopology _getTrainTopology;
        private readonly GenerateBookingReference _generateBookingReference;
        private readonly ConfirmReservation _confirmReservation;

        public MakeReservation(GetTrainTopology getTrainTopology, GenerateBookingReference generateBookingReference, ConfirmReservation confirmReservation)
        {
            _getTrainTopology = getTrainTopology;
            _generateBookingReference = generateBookingReference;
            _confirmReservation = confirmReservation;
        }

        public Maybe<Reservation> Execute(ReservationRequest request)
        {
            return _getTrainTopology.ById(request.TrainId)
                .Map(topology => GetAvailableCoaches(request, topology))
                .Map(availableCoach => GetAvailableSeats(request, availableCoach))
                .Map(availableSeats => Maybe.Some(ConfirmReservation(request, availableSeats)));
        }

        private static Maybe<Coach> GetAvailableCoaches(ReservationRequest request, Topology topology)
        {
            foreach (var coach in topology.Coaches)
            {
                var availableSeats = coach.Seats.Count(seat => seat.IsAvailable());
                if (availableSeats >= request.SeatsNumber)
                {

                    return Maybe.Some(coach);
                }
            }

            return Maybe.None<Coach>();
        }

        private static Maybe<List<SeatId>> GetAvailableSeats(ReservationRequest request, Coach availableCoach)
        {
            var availableSeats = availableCoach.Seats.Where(seat => seat.IsAvailable()).Select(seat => seat.Id).ToArray();
            return availableSeats.Length >= request.SeatsNumber
                ? Maybe.Some(availableSeats.Take(request.SeatsNumber).ToList())
                : Maybe.None<List<SeatId>>();
        }
        
        private Reservation ConfirmReservation(ReservationRequest request, List<SeatId> seats)
        {
            var reference = _generateBookingReference.Generate();
            var reservation = new Reservation(request.TrainId, reference, seats);
            _confirmReservation.Execute(reservation);
            return reservation;
        }
    }
}
