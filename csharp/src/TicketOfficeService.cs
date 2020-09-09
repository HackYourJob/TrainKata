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
            var seats = GetSeats(request);

            if (!(seats.Count == 0)) {
                ReservationResponseDto reservation = new ReservationResponseDto(request.TrainId, seats, bookingReferenceClient.GenerateBookingReference());
                bookingReferenceClient.BookTrain(reservation.TrainId, reservation.BookingId, reservation.Seats);
                return "{" +
                        "\"train_id\": \"" + reservation.TrainId + "\", " +
                        "\"booking_reference\": \"" + reservation.BookingId + "\", " +
                        "\"seats\": [" + String.Join(", ", reservation.Seats.Select(s => "\"" + s.SeatNumber + s.Coach + "\"")) + "]" +
                        "}";
            } else {
                return "{\"train_id\": \"" + request.TrainId + "\", \"booking_reference\": \"\", \"seats\": []}";
            }
        }

        private List<Seat> GetSeats(ReservationRequestDto request)
        {
            string data = trainDataClient.GetTopology(request.TrainId);

            var coachesByCoachId = DeserializeInToTopologie(data);

            var firstAvailableCoach = GetFirstAvailableCoach(request, coachesByCoachId);

            List<Seat> seats = new List<Seat>();
            if (!firstAvailableCoach.Equals(default(KeyValuePair<string, List<TopologieDto.TopologieSeatDto>>)))
            {
                long limit = request.SeatCount;
                foreach (TopologieDto.TopologieSeatDto seatDto in firstAvailableCoach.Value)
                {
                    if (IsSeatAvailable(seatDto))
                    {
                        if (limit-- == 0) break;
                        Seat seat = new Seat(seatDto.coach, seatDto.seat_number);
                        seats.Add(seat);
                    }
                }
            }

            return seats;
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
