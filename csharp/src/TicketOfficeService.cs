using System;
using System.Collections.Generic;
using System.Linq;
using TrainKata.Domain;
using TrainKata.Infra;

namespace TrainKata
{
    public class TicketOfficeService
    {
        private IBookingReferenceClient bookingReferenceClient;
        private MakeReservation _makeReservation;

        public TicketOfficeService(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            this.bookingReferenceClient = bookingReferenceClient;
            _makeReservation = new MakeReservation(new GetSncfTopology(trainDataClient), new BookSncfReservation(bookingReferenceClient));
        }

        public String MakeReservation(ReservationRequestDto request)
        {
            var trainId = new TrainId(request.TrainId);

            var reservation = _makeReservation.Execute(request.ToDomain());

            return SerializeReservation(reservation, trainId);
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

        private static string SerializeReservation(Reservation? reservation, TrainId trainId)
        {
            if (!reservation.HasValue)
            {
                return "{\"train_id\": \"" + trainId.Value + "\", \"booking_reference\": \"\", \"seats\": []}";
            }

            return "{" +
                   "\"train_id\": \"" + reservation.Value.TrainId.Value + "\", " +
                   "\"booking_reference\": \"" + reservation.Value.BookingId + "\", " +
                   "\"seats\": [" + String.Join(", ",
                       reservation.Value.Seats.Select(seatId => "\"" + seatId + "\"")) +
                   "]" +
                   "}";
        }
    }
}
