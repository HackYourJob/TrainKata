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
            var trainTopology = _trainDataClient.GetTopology(request.TrainId);

            var seachsByCoaches = new Dictionary<string, List<Topologie.TopologieSeat>>();
            foreach (var seat in JsonConvert.DeserializeObject<Topologie>(trainTopology).seats.Values)
            {
                if (!seachsByCoaches.ContainsKey(seat.coach)) seachsByCoaches.Add(seat.coach, new List<Topologie.TopologieSeat>());
                seachsByCoaches[seat.coach].Add(seat);
            }
            var availableSeatsByCoaches = default(KeyValuePair<string, List<Topologie.TopologieSeat>>);
            foreach (var coach in seachsByCoaches)
            {
                var availableSeats = 0L;
                foreach (var seat in coach.Value)
                {
                    if ("".Equals(seat.booking_reference)) {
                        availableSeats++;
                    }
                }
                if (availableSeats >= request.SeatCount) {
                    availableSeatsByCoaches = coach;
                    break;
                }
            }
            var seats = new List<Seat>();
            if(!availableSeatsByCoaches.Equals(default(KeyValuePair<string, List<Topologie.TopologieSeat>>))) {
                long limit = request.SeatCount;
                foreach (var seat in availableSeatsByCoaches.Value)
                {
                    if ("".Equals(seat.booking_reference)) {
                        if (limit-- == 0) break;
                        seats.Add(new Seat(seat.coach, seat.seat_number));
                    }
                }
            }

            if (seats.Count != 0) {
                var reservation = new ReservationResponseDto(request.TrainId, seats, _bookingReferenceClient.GenerateBookingReference());
                _bookingReferenceClient.BookTrain(reservation.TrainId, reservation.BookingId, reservation.Seats);
                return "{" +
                        "\"train_id\": \"" + reservation.TrainId + "\", " +
                        "\"booking_reference\": \"" + reservation.BookingId + "\", " +
                        "\"seats\": [" + String.Join(", ", reservation.Seats.Select(s => "\"" + s.SeatNumber + s.Coach + "\"")) + "]" +
                        "}";
            } else {
                return "{\"train_id\": \"" + request.TrainId + "\", \"booking_reference\": \"\", \"seats\": []}";
            }
        }
    }
}
