using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace KataTrainReservation
{
    public partial class TicketOfficeServiceInfra
    {

        private readonly ITrainDataClient trainDataClient;
        private readonly IBookingReferenceClient bookingReferenceClient;

        public TicketOfficeServiceInfra(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            this.trainDataClient = trainDataClient;
            this.bookingReferenceClient = bookingReferenceClient;
        }

        public String MakeReservation(ReservationRequestDto request)
        {
            var topologie = GetTopologie(request);

            var firstAvailableCoach = GetFirstAvailableCoach(request, topologie);
            var seats = SelectSeatsToBook(request, firstAvailableCoach);
            var reservation = MakeReservation(request, seats);
            
            return SerializeReservationResponse(reservation);
        }

        private Dictionary<string, List<TopologieDto.TopologieSeatDto>> GetTopologie(ReservationRequestDto request)
        {
            string data = trainDataClient.GetTopology(request.TrainId);

            var coachesByCoachId = DeserializeInToTopologie(data);
            return coachesByCoachId;
        }

        private ReservationResponseDto MakeReservation(ReservationRequestDto request, List<SeatDto> seats)
        {
            if (seats.Count == 0)
            {
                return ReservationResponseDto.Failed(request.TrainId);
            }

            var bookingReference = bookingReferenceClient.GenerateBookingReference();
            var reservation = ReservationResponseDto.Success(request.TrainId, seats, bookingReference);
            bookingReferenceClient.BookTrain(reservation.TrainId, reservation.BookingId, reservation.Seats);
            return reservation;
        }

        private static string SerializeReservationResponse(ReservationResponseDto reservation)
        {
            return "{" +
                   "\"train_id\": \"" + reservation.TrainId + "\", " +
                   "\"booking_reference\": \"" + reservation.BookingId + "\", " +
                   "\"seats\": [" + String.Join(", ",
                       reservation.Seats.Select(s => "\"" + s.SeatNumber + s.Coach + "\"")) +
                   "]" +
                   "}";
        }

        private static List<SeatDto> SelectSeatsToBook(ReservationRequestDto request, Coach? firstAvailableCoach)
        {
           return (firstAvailableCoach?.Seats ?? new List<Seat>()) 
                .Where(seat => seat.IsAvailable)
                .Take(request.SeatCount)
                .Select(seat => new SeatDto(seat.Id.CoachNumber, seat.Id.SeatNumber))
                .ToList();
        }

        private static Coach? GetFirstAvailableCoach(
            ReservationRequestDto request, 
            Dictionary<string, List<TopologieDto.TopologieSeatDto>> coachesByCoachId)
        {
            var topologie = new Topologie(coachesByCoachId.Values.Select(coachDto =>
            {
                return new Coach(
                    coachDto.Select(seatDto=> seatDto.ToSeat())
                        .ToList());
            }).ToList());
            
            
            return topologie.Coaches
                .FirstOrDefault(coach =>
                {
                    var seats = coach.Seats;
                    return seats.Count(seat => seat.IsAvailable) >= request.SeatCount;
                });
        }

        private static Dictionary<string, List<TopologieDto.TopologieSeatDto>> DeserializeInToTopologie(string data)
        {
            Dictionary<String, List<TopologieDto.TopologieSeatDto>> coachesByCoachId =
                new Dictionary<string, List<TopologieDto.TopologieSeatDto>>();
            foreach (var seat in JsonConvert.DeserializeObject<TopologieDto>(data).seats.Values)
            {
                if (!coachesByCoachId.ContainsKey(seat.coach))
                    coachesByCoachId.Add(seat.coach, new List<TopologieDto.TopologieSeatDto>());
                coachesByCoachId[seat.coach].Add(seat);
            }

            return coachesByCoachId;
        }

        private static bool IsSeatAvailable(TopologieDto.TopologieSeatDto y)
        {
            return "".Equals(y.booking_reference);
        }
    }
}
