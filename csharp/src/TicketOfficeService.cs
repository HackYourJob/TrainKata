using System;
using System.Collections.Generic;
using System.Linq;
using Newtonsoft.Json;
using TrainKata.Options;

namespace TrainKata
{
    public class TicketOfficeService
    {

        private readonly ITrainDataClient _trainDataClient;
        private readonly IBookingReferenceClient _bookingReferenceClient;

        public TicketOfficeService(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            _trainDataClient = trainDataClient;
            _bookingReferenceClient = bookingReferenceClient;
        }

        public string MakeReservation(ReservationRequestDto requestDto)
        {
            var request = requestDto.ToRequest();

            return
                GetTrainTopology(request)
                .Map(topology => GetAvailableCoaches(request, topology))
                .Map(availableCoach => GetAvailableSeats(request, availableCoach))
                .Map(availableSeats => Maybe.Some(ConfirmReservation(request, availableSeats)))
                .Map(reservation => Maybe.Some(ReturnConfirmedReservation(reservation)))
                .OrDefault(ReturnFailedReservation(request));
        }

        private Maybe<Topology> GetTrainTopology(ReservationRequest request)
        {
            var trainTopology = _trainDataClient.GetTopology(request.TrainId.Id);

            return Maybe.Some(DeserializeTrainTopology(trainTopology));
        }

        private static Topology DeserializeTrainTopology(string trainTopology)
        {
            return new Topology(
                JsonConvert.DeserializeObject<TopologieDto>(trainTopology)
                .seats.Values
                .GroupBy(seat => seat.coach)
                .Select(coach => new Coach(coach.Select(seat => seat.ToSeat()).ToArray()))
                .ToArray());;
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
            var reference = _bookingReferenceClient.GenerateBookingReference();
            var reservation = new Reservation(request.TrainId, new BookingReference(reference), seats);
            _bookingReferenceClient.BookTrain(reservation.TrainId.Id, reservation.BookingReference.Reference, reservation.Seats.Select(id => new SeatDto(id)).ToList());
            return reservation;
        }

        private static string ReturnConfirmedReservation(Reservation reservation)
        {
            return "{" +
                   "\"train_id\": \"" + reservation.TrainId + "\", " +
                   "\"booking_reference\": \"" + reservation.BookingReference + "\", " +
                   "\"seats\": [" + String.Join(", ", reservation.Seats.Select(id => "\"" + id + "\"")) + "]" +
                   "}";
        }

        private static string ReturnFailedReservation(ReservationRequest request)
        {
            return "{\"train_id\": \"" + request.TrainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }
    }
}
