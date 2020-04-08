using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace KataTrainReservation
{
    public struct TrainId
    {
        public string Value { get; }

        public TrainId(string value)
        {
            Value = value;
        }
    }

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
            var trainId = new TrainId(request.TrainId);

            var train = GetTopology(trainId);
            var foundCoach = FindCoachWithEnoughtAvailableSeats(request.SeatCount, train);
            var availableSeats = FindAvailableSeats(request.SeatCount, foundCoach);

            var reservation = Book(trainId, availableSeats);
            return SerializeReservation(reservation);
        }

        private Topology GetTopology(TrainId trainId)
        {
            string data = trainDataClient.GetTopology(trainId.Value);

            return SetupTrain(data);
        }

        private static string SerializeReservation(ReservationResponseDto reservation)
        {
            return "{" +
                   "\"train_id\": \"" + reservation.TrainId + "\", " +
                   "\"booking_reference\": \"" + reservation.BookingId + "\", " +
                   "\"seats\": [" + String.Join(", ",
                       reservation.Seats.Select(s => "\"" + s.SeatNumber + s.Coach + "\"")) +
                   "]" +
                   "}";
        }

        private ReservationResponseDto Book(TrainId trainId, List<Seat> availableSeats)
        {
            if (availableSeats.Any())
            {
                ReservationResponseDto reservation = new ReservationResponseDto(trainId.Value, availableSeats.Select(seat => new SeatDto(seat.CoachId, seat.SeatNumber)).ToList(),
                    bookingReferenceClient.GenerateBookingReference());
                bookingReferenceClient.BookTrain(reservation.TrainId, reservation.BookingId, reservation.Seats);
                return reservation;
            }

            return new ReservationResponseDto(trainId.Value, new List<SeatDto>(), "");
        }

        private static List<Seat> FindAvailableSeats(int requestSeatCount, Coach? foundCoach)
        {
            if (foundCoach.HasValue)
            {
                return foundCoach.Value.Seats
                    .Where(seat => seat.IsAvailable)
                    .Take(requestSeatCount)
                    .ToList();
            }

            return new List<Seat>();
        }

        private static Coach? FindCoachWithEnoughtAvailableSeats(int seatCount, Topology train)
        {
            return train.Coaches
                .Where(coach => coach.Seats.Count(s => s.IsAvailable) >= seatCount)
                .Select(c => (Coach?)c)
                .FirstOrDefault();
        }

        private static Topology SetupTrain(string sncfTopologyJson)
        {
            Dictionary<String, List<TopologieDto.TopologieSeatDto>> train = new Dictionary<string, List<TopologieDto.TopologieSeatDto>>();
            var coaches = 
                Deserialize(sncfTopologyJson).GroupBy(o => o.coach).Select(values =>
                new Coach(values.Select(s => new Seat(s.booking_reference, s.coach, s.seat_number)).ToList()))
                .ToList();
            foreach (TopologieDto.TopologieSeatDto seat in Deserialize(sncfTopologyJson))
            {
                if (!train.ContainsKey(seat.coach)) train.Add(seat.coach, new List<TopologieDto.TopologieSeatDto>());
                train[seat.coach].Add(seat);
            }

            return new Topology(train, coaches);
        }

        private static Dictionary<string, TopologieDto.TopologieSeatDto>.ValueCollection Deserialize(string sncfTopologyJson)
        {
            return JsonConvert.DeserializeObject<TopologieDto>(sncfTopologyJson).seats.Values;
        }
    }

    public struct Topology
    {
        public Dictionary<string, List<TopologieDto.TopologieSeatDto>> Legacy { get; }

        public List<Coach> Coaches { get; }

        public Topology(Dictionary<string, List<TopologieDto.TopologieSeatDto>> legacy, List<Coach> coaches)
        {
            Legacy = legacy;
            Coaches = coaches;
        }
    }
    public struct Coach
    {
        public List<Seat> Seats { get; }

        public Coach(List<Seat> seats)
        {
            Seats = seats;
        }
    }

    public struct Seat
    {
        public bool IsAvailable { get; }
        public string CoachId { get; }
        public int SeatNumber { get; }

        public Seat(string bookingReference, string coachId, int seatNumber)
        {
            CoachId = coachId;
            SeatNumber = seatNumber;
            IsAvailable = string.IsNullOrEmpty(bookingReference);
        }
    }
}
