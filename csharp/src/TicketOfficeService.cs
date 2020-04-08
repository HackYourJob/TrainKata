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

            var topology = GetTopology(trainId);
            var availableSeats = FindAvailableSeats(request.SeatCount, topology);

            var reservation = Book(trainId, availableSeats);
            return SerializeReservation(reservation);
        }

        private Topology GetTopology(TrainId trainId)
        {
            string sncfTopologyJson = trainDataClient.GetTopology(trainId.Value);

            return SetupTrain(sncfTopologyJson);
        }

        private static Topology SetupTrain(string sncfTopologyJson)
        {
            var coaches = 
                Deserialize(sncfTopologyJson).GroupBy(o => o.coach).Select(values =>
                        new Coach(values.Select(s => new Seat(s.booking_reference, s.coach, s.seat_number)).ToList()))
                    .ToList();

            return new Topology(coaches);
        }

        private static Dictionary<string, TopologieDto.TopologieSeatDto>.ValueCollection Deserialize(string sncfTopologyJson)
        {
            return JsonConvert.DeserializeObject<TopologieDto>(sncfTopologyJson).seats.Values;
        }

        private static List<Seat> FindAvailableSeats(int seatCount, Topology topology)
        {
            return topology.FindAvailableSeats(seatCount);
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
    }

    public struct Topology
    {
        public List<Coach> Coaches { get; }

        public Topology(List<Coach> coaches)
        {
            Coaches = coaches;
        }

        public List<Seat> FindAvailableSeats(int seatCount)
        {
            return Coaches
                .Where(coach => coach.Seats.Count(s => s.IsAvailable) >= seatCount)
                .Select(c => c.FindAvailableSeats(seatCount))
                .FirstOrDefault() ?? new List<Seat>();
        }
    }
    public struct Coach
    {
        public List<Seat> Seats { get; }

        public Coach(List<Seat> seats)
        {
            Seats = seats;
        }

        public List<Seat> FindAvailableSeats(int requestSeatCount)
        {
            return Seats
                .Where(seat => seat.IsAvailable)
                .Take(requestSeatCount)
                .ToList();
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
