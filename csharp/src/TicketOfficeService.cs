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

            var coachesByCoachId = DeserializeInToTopologie(data);

            var firstAvailableCoach = GetFirstAvailableCoach(request, coachesByCoachId);
            var seats = SelectSeatsToBook(request, firstAvailableCoach);
            return BookSeats(request, seats);
        }

        private string BookSeats(ReservationRequestDto request, List<Seat> seats)
        {
            if (!(seats.Count == 0))
            {
                ReservationResponseDto reservation =
                    new ReservationResponseDto(request.TrainId, seats, bookingReferenceClient.GenerateBookingReference());
                bookingReferenceClient.BookTrain(reservation.TrainId, reservation.BookingId, reservation.Seats);
                return "{" +
                       "\"train_id\": \"" + reservation.TrainId + "\", " +
                       "\"booking_reference\": \"" + reservation.BookingId + "\", " +
                       "\"seats\": [" + String.Join(", ", reservation.Seats.Select(s => "\"" + s.SeatNumber + s.Coach + "\"")) +
                       "]" +
                       "}";
            }
            else
            {
                return "{\"train_id\": \"" + request.TrainId + "\", \"booking_reference\": \"\", \"seats\": []}";
            }
        }

        private List<Seat> GetSeats(ReservationRequestDto request)
        {
            string data = trainDataClient.GetTopology(request.TrainId);

            var coachesByCoachId = DeserializeInToTopologie(data);

            var firstAvailableCoach = GetFirstAvailableCoach(request, coachesByCoachId);

            var seats = SelectSeatsToBook(request, firstAvailableCoach);

            return seats;
        }

        private static List<Seat> SelectSeatsToBook(ReservationRequestDto request, KeyValuePair<string, List<TopologieDto.TopologieSeatDto>> firstAvailableCoach)
        {
           return (firstAvailableCoach.Value ?? new List<TopologieDto.TopologieSeatDto>())
                .Where(IsSeatAvailable)
                .Take(request.SeatCount)
                .Select(seat => new Seat(seat.coach, seat.seat_number))
                .ToList();
        }

        private static KeyValuePair<string, List<TopologieDto.TopologieSeatDto>> GetFirstAvailableCoach(ReservationRequestDto request, Dictionary<string, List<TopologieDto.TopologieSeatDto>> coachesByCoachId)
        {
            return coachesByCoachId
                .FirstOrDefault(coach =>
                {
                    var seats = coach.Value;
                    return seats.Count(IsSeatAvailable) >= request.SeatCount;
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
