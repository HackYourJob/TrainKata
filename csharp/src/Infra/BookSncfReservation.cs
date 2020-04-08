using System.Collections.Generic;
using System.Linq;
using TrainKata.Domain;
using TrainKata.Infra;

namespace TrainKata
{
    public class BookSncfReservation : BookReservation
    {
        private readonly IBookingReferenceClient _bookingReferenceClient;

        public BookSncfReservation(IBookingReferenceClient bookingReferenceClient)
        {
            _bookingReferenceClient = bookingReferenceClient;
        }

        public Reservation? Book(TrainId trainId, List<Seat> availableSeats)
        {
            if (availableSeats.Any())
            {
                ReservationResponseDto reservation = new ReservationResponseDto(trainId.Value, availableSeats.Select(seat => new SeatDto(seat.Id)).ToList(), _bookingReferenceClient.GenerateBookingReference());
                _bookingReferenceClient.BookTrain(reservation.TrainId, reservation.BookingId, reservation.Seats);
                return reservation.ToDomain();
            }

            return null;
        }
    }
}