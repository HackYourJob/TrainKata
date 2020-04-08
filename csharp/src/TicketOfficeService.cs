using System;
using System.Collections.Generic;
using System.Linq;
using Newtonsoft.Json;
using TrainKata.Domain;
using TrainKata.Infra;

namespace TrainKata
{
    public class TicketOfficeService
    {

        private ITrainDataClient trainDataClient;
        private IBookingReferenceClient bookingReferenceClient;
        private MakeReservation _makeReservation;

        public TicketOfficeService(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            this.trainDataClient = trainDataClient;
            this.bookingReferenceClient = bookingReferenceClient;
            _makeReservation = new MakeReservation(new GetSncfTopology(trainDataClient));
        }

        public String MakeReservation(ReservationRequestDto request)
        {
            var trainId = new TrainId(request.TrainId);

            var topology = GetTopology(trainId);

            var availableSeats = _makeReservation.TryToFindAvailableSeats(request.ToDomain(), topology);
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
                        new Coach(values.Select(s => new Seat(new SeatId(s.coach, s.seat_number), string.IsNullOrEmpty(s.booking_reference))).ToList()))
                    .ToList();

            return new Topology(coaches);
        }

        private static Dictionary<string, TopologieDto.TopologieSeatDto>.ValueCollection Deserialize(string sncfTopologyJson)
        {
            return JsonConvert.DeserializeObject<TopologieDto>(sncfTopologyJson).seats.Values;
        }

        private ReservationResponseDto Book(TrainId trainId, List<Seat> availableSeats)
        {
            if (availableSeats.Any())
            {
                ReservationResponseDto reservation = new ReservationResponseDto(trainId.Value, availableSeats.Select(seat => new SeatDto(seat.Id)).ToList(),
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
}
