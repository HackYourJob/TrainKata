using System;
using System.Collections.Generic;
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
            return GetTopologyOf(request.TrainId)
                .Map(TryToReserveSeats(request))
                .Map(ConfirmReservation(request));
        }

        private Maybe<Topology> GetTopologyOf(TrainId id)
        {
            return _getTrainTopology.ById(id);
        }

        private static Func<Topology, Maybe<List<SeatId>>> TryToReserveSeats(ReservationRequest request)
        {
            return topology => topology.TryToReserveSeats(request.SeatsNumber);
        }

        private Func<List<SeatId>, Maybe<Reservation>> ConfirmReservation(ReservationRequest request)
        {
            return seats =>
            {
                var reference = _generateBookingReference.Generate();
                var reservation = new Reservation(request.TrainId, reference, seats);
                _confirmReservation.Execute(reservation);

                return Maybe.Some(reservation);
            };
        }
    }
}
