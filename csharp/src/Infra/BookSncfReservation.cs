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
                var bookingReference = _bookingReferenceClient.GenerateBookingReference();

                _bookingReferenceClient.BookTrain(trainId.Value, bookingReference, availableSeats.Select(seat => new SeatDto(seat.Id)).ToList());
                
                return new Reservation(trainId, availableSeats.Select(s => s.Id).ToList(), bookingReference);
            }

            return null;
        }
    }
}