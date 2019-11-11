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

        private Dictionary<string, List<Topologie.TopologieSeat>> GetTrainTopology(ReservationRequestDto request)
        {
            var trainTopology = _trainDataClient.GetTopology(request.TrainId);

            var seachsByCoaches = DeserializeTrainTopology(trainTopology);
            return seachsByCoaches;
        }

        private static Dictionary<string, List<Topologie.TopologieSeat>> DeserializeTrainTopology(string trainTopology)
        {
            var seachsByCoaches = new Dictionary<string, List<Topologie.TopologieSeat>>();
            foreach (var seat in JsonConvert.DeserializeObject<Topologie>(trainTopology).seats.Values)
            {
                if (!seachsByCoaches.ContainsKey(seat.coach))
                    seachsByCoaches.Add(seat.coach, new List<Topologie.TopologieSeat>());
                seachsByCoaches[seat.coach].Add(seat);
            }

            return seachsByCoaches;
        }

        private static KeyValuePair<string, List<Topologie.TopologieSeat>>? GetAvailableCoaches(ReservationRequestDto request, Dictionary<string, List<Topologie.TopologieSeat>> seachsByCoaches)
        {
            KeyValuePair<string, List<Topologie.TopologieSeat>>? availableSeatsByCoaches = null;
            foreach (var coach in seachsByCoaches)
            {
                var availableSeats = 0L;
                foreach (var seat in coach.Value)
                {
                    if ("".Equals(seat.booking_reference))
                    {
                        availableSeats++;
                    }
                }

                if (availableSeats >= request.SeatCount)
                {
                    availableSeatsByCoaches = coach;
                    break;
                }
            }

            return availableSeatsByCoaches;
        }

        private static List<Seat> GetAvailableSeats(ReservationRequestDto request, KeyValuePair<string, List<Topologie.TopologieSeat>>? availableSeatsByCoaches)
        {
            var seats = new List<Seat>();
            if (availableSeatsByCoaches.HasValue)
            {
                long limit = request.SeatCount;
                foreach (var seat in availableSeatsByCoaches.Value.Value)
                {
                    if ("".Equals(seat.booking_reference))
                    {
                        if (limit-- == 0) break;
                        seats.Add(new Seat(seat.coach, seat.seat_number));
                    }
                }
            }

            return seats;
        }

        private static bool HasReservation(List<Seat> seats)
        {
            return seats.Count != 0;
        }

        private ReservationResponseDto ConfirmReservation(ReservationRequestDto request, List<Seat> seats)
        {
            var reservation =
                new ReservationResponseDto(request.TrainId, seats, _bookingReferenceClient.GenerateBookingReference());
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
