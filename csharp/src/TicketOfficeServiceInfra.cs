using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace KataTrainReservation
{
    public class TicketOfficeServiceInfra
    {

        private readonly ITrainDataClient trainDataClient;
        private readonly IBookingReferenceClient bookingReferenceClient;
        private BookingTrainAdapter _bookingTrain;

        public TicketOfficeServiceInfra(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            this.trainDataClient = trainDataClient;
            this.bookingReferenceClient = bookingReferenceClient;
            _bookingTrain = new BookingTrainAdapter(bookingReferenceClient);
        }

        public String MakeReservation(ReservationRequestDto request)
        {
            var topologieDto = GetTopologie(request);

            var reservationRequest = request.ToReservationRequest();
            var topologie = new Topologie(topologieDto.Values.Select(coachDto =>
            {
                return new Coach(
                    coachDto.Select(seatDto=> seatDto.ToSeat())
                        .ToList());
            }).ToList());
            
            var firstAvailableCoach = GetFirstAvailableCoach(reservationRequest, topologie);
            var seats = SelectSeatsToBook(reservationRequest, firstAvailableCoach).ToList();
            var reservation = MakeReservation(reservationRequest, seats);
            
            return SerializeReservationResponse(ReservationResponseDto.FromReservation(reservationRequest,reservation));
        }

        private Dictionary<string, List<TopologieDto.TopologieSeatDto>> GetTopologie(ReservationRequestDto request)
        {
            string data = trainDataClient.GetTopology(request.TrainId);

            var coachesByCoachId = DeserializeInToTopologie(data);
            return coachesByCoachId;
        }

        private Reservation? MakeReservation(ReservationRequest request, List<Seat> seats)
        {
            if (seats.Count == 0)
            {
                return default;
            }

            var bookingReference = _bookingTrain.Book(request.TrainId, seats.Select(seat=>seat.Id).ToList());
            return new Reservation(request.TrainId, bookingReference, seats.Select(seat=>seat.Id));
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

        private static IEnumerable<Seat> SelectSeatsToBook(ReservationRequest request, Coach? firstAvailableCoach)
        {
           return (firstAvailableCoach?.Seats ?? new List<Seat>()) 
                .Where(seat => seat.IsAvailable)
                .Take(request.SeatCount);
        }

        private static Coach? GetFirstAvailableCoach(
            ReservationRequest request, 
            Topologie topologie)
        {
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
