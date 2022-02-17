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
            var seatsByCoachId = GetTopology(request);
            
            var selectedCoach = SelectCoach(request, seatsByCoachId);
            
            var seats = SelectSeats(request, selectedCoach);

            return BookingSeats(request, seats);
        }

        private Dictionary<string, List<Topologie.TopologieSeat>> GetTopology(ReservationRequestDto request)
        {
            var serializedTopology = trainDataClient.GetTopology(request.TrainId);

            var seatsByCoachId = new Dictionary<string, List<Topologie.TopologieSeat>>();
            foreach (var seat in JsonConvert.DeserializeObject<Topologie>(serializedTopology).seats.Values)
            {
                if (!seatsByCoachId.ContainsKey(seat.coach))
                    seatsByCoachId.Add(seat.coach, new List<Topologie.TopologieSeat>());
                seatsByCoachId[seat.coach].Add(seat);
            }

            return seatsByCoachId;
        }

        private static KeyValuePair<string, List<Topologie.TopologieSeat>> SelectCoach(ReservationRequestDto request, Dictionary<string, List<Topologie.TopologieSeat>> seatsByCoachId)
        {
            var selectedCoach =
                default(KeyValuePair<string, List<Topologie.TopologieSeat>>);
            foreach (var coach in seatsByCoachId)
            {
                var freeSeat = 0L;
                foreach (var seat in coach.Value)
                {
                    if ("".Equals(seat.booking_reference))
                    {
                        freeSeat++;
                    }
                }

                var askedSeatNbInReservation = request.SeatCount;
                if (freeSeat >= askedSeatNbInReservation)
                {
                    selectedCoach = coach;
                    break;
                }
            }

            return selectedCoach;
        }

        private static List<Seat> SelectSeats(ReservationRequestDto request, KeyValuePair<string, List<Topologie.TopologieSeat>> selectedCoach)
        {
            var seats = new List<Seat>();
            if (!selectedCoach.Equals(default(KeyValuePair<string, List<Topologie.TopologieSeat>>)))
            {
                var selectedSeats = new List<Seat>();
                long limit = request.SeatCount;
                foreach (var seat in selectedCoach.Value)
                {
                    if ("".Equals(seat.booking_reference))
                    {
                        if (limit-- == 0) break;
                        var selectedSeat = new Seat(seat.coach, seat.seat_number);
                        selectedSeats.Add(selectedSeat);
                    }
                }

                seats = selectedSeats;
            }

            return seats;
        }

        private string BookingSeats(ReservationRequestDto request, List<Seat> seats)
        {
            if (seats.Any())
            {
                var reservation =
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
    }
}
