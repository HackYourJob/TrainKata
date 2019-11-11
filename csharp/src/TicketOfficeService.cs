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
            var data = _trainDataClient.GetTopology(request.TrainId);

            var map = new Dictionary<string, List<Topologie.TopologieSeat>>();
            foreach (var x in JsonConvert.DeserializeObject<Topologie>(data).seats.Values)
            {
                if (!map.ContainsKey(x.coach)) map.Add(x.coach, new List<Topologie.TopologieSeat>());
                map[x.coach].Add(x);
            }
            var found = default(KeyValuePair<string, List<Topologie.TopologieSeat>>);
            foreach (var x in map)
            {
                var count = 0L;
                foreach (var y in x.Value)
                {
                    if ("".Equals(y.booking_reference)) {
                        count++;
                    }
                }
                if (count >= request.SeatCount) {
                    found = x;
                    break;
                }
            }
            var seats = new List<Seat>();
            if(!found.Equals(default(KeyValuePair<string, List<Topologie.TopologieSeat>>))) {
                var list = new List<Seat>();
                long limit = request.SeatCount;
                foreach (var y in found.Value)
                {
                    if ("".Equals(y.booking_reference)) {
                        if (limit-- == 0) break;
                        var seat = new Seat(y.coach, y.seat_number);
                        list.Add(seat);
                    }
                }
                seats = list;
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
