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

            Dictionary<String, List<TopologieDto.TopologieSeatDto>> coachesByCoachId =
                new Dictionary<string, List<TopologieDto.TopologieSeatDto>>();
            foreach (TopologieDto.TopologieSeatDto x in JsonConvert.DeserializeObject<TopologieDto>(data).seats.Values)
            {
                if (!coachesByCoachId.ContainsKey(x.coach)) coachesByCoachId.Add(x.coach, new List<TopologieDto.TopologieSeatDto>());
                coachesByCoachId[x.coach].Add(x);
            }

            KeyValuePair<string, List<TopologieDto.TopologieSeatDto>> firstAvailableCoach =
                default(KeyValuePair<string, List<TopologieDto.TopologieSeatDto>>);
            foreach (KeyValuePair<string, List<TopologieDto.TopologieSeatDto>> coach in coachesByCoachId)
            {
                long availableSeats = 0L;
                foreach (TopologieDto.TopologieSeatDto y in coach.Value)
                {
                    if ("".Equals(y.booking_reference))
                    {
                        availableSeats++;
                    }
                }

                if (availableSeats >= request.SeatCount)
                {
                    firstAvailableCoach = coach;
                    break;
                }
            }

            List<Seat> seats = new List<Seat>();
            if (!firstAvailableCoach.Equals(default(KeyValuePair<string, List<TopologieDto.TopologieSeatDto>>)))
            {
                long limit = request.SeatCount;
                foreach (TopologieDto.TopologieSeatDto y in firstAvailableCoach.Value)
                {
                    if ("".Equals(y.booking_reference))
                    {
                        if (limit-- == 0) break;
                        Seat seat = new Seat(y.coach, y.seat_number);
                        seats.Add(seat);
                    }
                }
            }

            return seats;
        }
    }
}
