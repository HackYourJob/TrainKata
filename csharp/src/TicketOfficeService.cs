using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace KataTrainReservation
{
    public class TicketOfficeService
    {

        private ITrainDataClient trainDataClient;
        private IBookingReferenceClient bookingReferenceClient;

        public TicketOfficeService(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            this.trainDataClient = trainDataClient;
            this.bookingReferenceClient = bookingReferenceClient;
        }

        public String MakeReservation(ReservationRequestDto request)
        {
            string data = trainDataClient.GetTopology(request.TrainId);

            Dictionary<String, List<Topologie.TopologieSeat>> map = new Dictionary<string, List<Topologie.TopologieSeat>>();
            foreach (Topologie.TopologieSeat x in JsonConvert.DeserializeObject<Topologie>(data).seats.Values)
            {
                if (!map.ContainsKey(x.coach)) map.Add(x.coach, new List<Topologie.TopologieSeat>());
                map[x.coach].Add(x);
            }
            KeyValuePair<string, List<Topologie.TopologieSeat>> found = default(KeyValuePair<string, List<Topologie.TopologieSeat>>);
            foreach (KeyValuePair<string, List<Topologie.TopologieSeat>> x in map)
            {
                long count = 0L;
                foreach (Topologie.TopologieSeat y in x.Value)
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
            List<Seat> seats = new List<Seat>();
            if(!found.Equals(default(KeyValuePair<string, List<Topologie.TopologieSeat>>))) {
                List<Seat> list = new List<Seat>();
                long limit = request.SeatCount;
                foreach (Topologie.TopologieSeat y in found.Value)
                {
                    if ("".Equals(y.booking_reference)) {
                        if (limit-- == 0) break;
                        Seat seat = new Seat(y.coach, y.seat_number);
                        list.Add(seat);
                    }
                }
                seats = list;
            }

            if (!(seats.Count == 0)) {
                ReservationResponseDto reservation = new ReservationResponseDto(request.TrainId, seats, bookingReferenceClient.GenerateBookingReference());
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
