using System.Linq;
using TrainKata.Domain;
using TrainKata.Domain.PortOut;

namespace TrainKata.Infra.BookingReferenceClient
{
    public class BookingReferenceClientAdaptater : ConfirmReservation, GenerateBookingReference
    {
        private readonly IBookingReferenceClient _bookingReferenceClient;

        public BookingReferenceClientAdaptater(IBookingReferenceClient bookingReferenceClient)
        {
            _bookingReferenceClient = bookingReferenceClient;
        }

        public void Execute(Reservation reservation)
        {
            _bookingReferenceClient.BookTrain(reservation.TrainId.Id, reservation.BookingReference.Reference, reservation.Seats.Select(id => new SeatDto(id)).ToList());
        }

        public BookingReference Generate()
        {
            return new BookingReference(_bookingReferenceClient.GenerateBookingReference());
        }
    }
}
