using System;
using System.Collections.Generic;
using System.Linq;
using Newtonsoft.Json;

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
            var seachsByCoaches = GetTrainTopology(request);
            var availableSeatsByCoaches = GetAvailableCoaches(request, seachsByCoaches);
            var seats = GetAvailableSeats(request, availableSeatsByCoaches);

            if (HasReservation(seats))
            {
                var reservation = ConfirmReservation(request, seats);
                return ReturnConfirmedReservation(reservation);
            } 
            else 
            {
                return ReturnFailedReservation(request);
            }
        }

        private Dictionary<string, List<TopologieDto.TopologieSeatDto>> GetTrainTopology(ReservationRequestDto request)
        {
            var trainTopology = _trainDataClient.GetTopology(request.TrainId);

            return DeserializeTrainTopology(trainTopology);
        }

        private static Dictionary<string, List<TopologieDto.TopologieSeatDto>> DeserializeTrainTopology(string trainTopology)
        {
            return JsonConvert.DeserializeObject<TopologieDto>(trainTopology)
                .seats.Values
                .GroupBy(seat => seat.coach)
                .ToDictionary(coach => coach.Key, coach => coach.ToList());
        }

        private static KeyValuePair<string, List<TopologieDto.TopologieSeatDto>>? GetAvailableCoaches(ReservationRequestDto request, Dictionary<string, List<TopologieDto.TopologieSeatDto>> seachsByCoaches)
        {
            foreach (var coach in seachsByCoaches)
            {
                var availableSeats = coach.Value.Count(IsNotReserved);
                if (availableSeats >= request.SeatCount)
                {
                    return coach;
                }
            }

            return null;
        }

        private static List<SeatDto> GetAvailableSeats(ReservationRequestDto request, KeyValuePair<string, List<TopologieDto.TopologieSeatDto>>? availableSeatsByCoaches)
        {
            if (!availableSeatsByCoaches.HasValue) return new List<SeatDto>();

            var availableSeats = availableSeatsByCoaches.Value.Value.Where(IsNotReserved).ToArray();
            return availableSeats.Length >= request.SeatCount 
                ? availableSeats.Take(request.SeatCount).Select(seat => new SeatDto(seat.coach, seat.seat_number)).ToList() 
                : new List<SeatDto>();
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
