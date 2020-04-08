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

            var train = SetupTrain(data);
            var availableCoach = FindCoachWithEnoughtAvailableSeats(request, train);
            var availableSeats = FindAvailableSeats(request, availableCoach);

            return TryBook(request, availableSeats);
        }

        private string TryBook(ReservationRequestDto request, List<Seat> availableSeats)
        {
            if (availableSeats.Any())
            {
                ReservationResponseDto reservation = new ReservationResponseDto(request.TrainId, availableSeats,
                    bookingReferenceClient.GenerateBookingReference());
                bookingReferenceClient.BookTrain(reservation.TrainId, reservation.BookingId, reservation.Seats);
                return "{" +
                       "\"train_id\": \"" + reservation.TrainId + "\", " +
                       "\"booking_reference\": \"" + reservation.BookingId + "\", " +
                       "\"seats\": [" + String.Join(", ", reservation.Seats.Select(s => "\"" + s.SeatNumber + s.Coach + "\"")) +
                       "]" +
                       "}";
            }

            return "{\"train_id\": \"" + request.TrainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }

        private static List<Seat> FindAvailableSeats(ReservationRequestDto request, KeyValuePair<string, List<TopologieDto.TopologieSeatDto>> found)
        {
            List<Seat> seats = new List<Seat>();
            if (!found.Equals(default(KeyValuePair<string, List<TopologieDto.TopologieSeatDto>>)))
            {
                List<Seat> list = new List<Seat>();
                long limit = request.SeatCount;
                foreach (TopologieDto.TopologieSeatDto y in found.Value)
                {
                    if ("".Equals(y.booking_reference))
                    {
                        if (limit-- == 0) break;
                        Seat seat = new Seat(y.coach, y.seat_number);
                        list.Add(seat);
                    }
                }

                seats = list;
            }

            return seats;
        }

        private static KeyValuePair<string, List<TopologieDto.TopologieSeatDto>> FindCoachWithEnoughtAvailableSeats(ReservationRequestDto request, Dictionary<string, List<TopologieDto.TopologieSeatDto>> train)
        {
            KeyValuePair<string, List<TopologieDto.TopologieSeatDto>> found = default;
            foreach (KeyValuePair<string, List<TopologieDto.TopologieSeatDto>> coach in train)
            {
                long count = 0L;
                foreach (TopologieDto.TopologieSeatDto seat in coach.Value)
                {
                    if (string.Empty.Equals(seat.booking_reference))
                    {
                        count++;
                    }
                }

                if (count >= request.SeatCount)
                {
                    found = coach;
                    break;
                }
            }

            return found;
        }

        private static Dictionary<string, List<TopologieDto.TopologieSeatDto>> SetupTrain(string sncfTopologyJson)
        {
            Dictionary<String, List<TopologieDto.TopologieSeatDto>> train = new Dictionary<string, List<TopologieDto.TopologieSeatDto>>();
            var coaches = 
                Deserialize(sncfTopologyJson).GroupBy(o => o.coach).Select(values =>
                new Coach(values.Select(s => new Seat(s.coach, s.seat_number)).ToList()))
                .ToList();
            foreach (TopologieDto.TopologieSeatDto seat in Deserialize(sncfTopologyJson))
            {
                if (!train.ContainsKey(seat.coach)) train.Add(seat.coach, new List<TopologieDto.TopologieSeatDto>());
                train[seat.coach].Add(seat);
            }

            return train;
        }

        private static Dictionary<string, TopologieDto.TopologieSeatDto>.ValueCollection Deserialize(string sncfTopologyJson)
        {
            return JsonConvert.DeserializeObject<TopologieDto>(sncfTopologyJson).seats.Values;
        }
    }

    public class Topology
    {
        public List<Coach> Coaches { get; } = new List<Coach>();
    }
    public class Coach
    {
        public List<Seat> Seats { get; }

        public Coach(List<Seat> seats)
        {
            Seats = seats;
        }
    }
}
