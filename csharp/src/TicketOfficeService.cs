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

        public string MakeReservation(ReservationRequestDto request)
        {
            return
                GetTrainTopology(request)
                .Map(topology => GetAvailableCoaches(request, topology))
                .Map(availableCoach => GetAvailableSeats(request, availableCoach))
                .Map(availableSeats => Maybe.Some(ConfirmReservation(request, availableSeats)))
                .Map(reservation => Maybe.Some(ReturnConfirmedReservation(reservation)))
                .OrDefault(ReturnFailedReservation(request));
        }

        private Maybe<Dictionary<string, List<TopologieDto.TopologieSeatDto>>> GetTrainTopology(ReservationRequestDto request)
        {
            var trainTopology = _trainDataClient.GetTopology(request.TrainId);

            return Maybe.Some(DeserializeTrainTopology(trainTopology));
        }

        private static Dictionary<string, List<TopologieDto.TopologieSeatDto>> DeserializeTrainTopology(string trainTopology)
        {
            return JsonConvert.DeserializeObject<TopologieDto>(trainTopology)
                .seats.Values
                .GroupBy(seat => seat.coach)
                .ToDictionary(coach => coach.Key, coach => coach.ToList());
        }

        private static Maybe<List<TopologieDto.TopologieSeatDto>> GetAvailableCoaches(ReservationRequestDto request, Dictionary<string, List<TopologieDto.TopologieSeatDto>> topology)
        {
            foreach (var coach in topology)
            {
                var availableSeats = coach.Value.Count(IsNotReserved);
                if (availableSeats >= request.SeatCount)
                {
                    return Maybe.Some(coach.Value);
                }
            }

            return Maybe.None<List<TopologieDto.TopologieSeatDto>>();
        }

        private static Maybe<List<SeatDto>> GetAvailableSeats(ReservationRequestDto request, List<TopologieDto.TopologieSeatDto> availableCoach)
        {
            var availableSeats = availableCoach.Where(IsNotReserved).ToArray();
            return availableSeats.Length >= request.SeatCount
                ? Maybe.Some(availableSeats.Take(request.SeatCount)
                    .Select(seat => new SeatDto(seat.coach, seat.seat_number)).ToList())
                : Maybe.None<List<SeatDto>>();
        }

        private static bool IsNotReserved(TopologieDto.TopologieSeatDto seatDto)
        {
            return "".Equals(seatDto.booking_reference);
        }

        private static bool HasReservation(List<SeatDto> seats)
        {
            return seats.Count != 0;
        }

        private ReservationResponseDto ConfirmReservation(ReservationRequestDto request, List<SeatDto> seats)
        {
            var bookingId = _bookingReferenceClient.GenerateBookingReference();
            var reservation = new ReservationResponseDto(request.TrainId, seats, bookingId);
            _bookingReferenceClient.BookTrain(reservation.TrainId, reservation.BookingId, reservation.Seats);
            return reservation;
        }

        private static string ReturnConfirmedReservation(ReservationResponseDto reservation)
        {
            return "{" +
                   "\"train_id\": \"" + reservation.TrainId + "\", " +
                   "\"booking_reference\": \"" + reservation.BookingId + "\", " +
                   "\"seats\": [" + String.Join(", ", reservation.Seats.Select(s => "\"" + s.SeatNumber + s.Coach + "\"")) + "]" +
                   "}";
        }

        private static string ReturnFailedReservation(ReservationRequestDto request)
        {
            return "{\"train_id\": \"" + request.TrainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }
    }
}
