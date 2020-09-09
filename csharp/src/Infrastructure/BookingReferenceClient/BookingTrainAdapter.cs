using System.Collections.Generic;
using System.Linq;

namespace KataTrainReservation
{
    public class BookingTrainAdapter : BookingTrain
    {
        private readonly IBookingReferenceClient _bookingReferenceClient;

        public BookingTrainAdapter(IBookingReferenceClient bookingReferenceClient)
        {
            _bookingReferenceClient = bookingReferenceClient;
        }

        public BookingReference Book(TrainId trainId, IList<SeatId> seats)
        {
            var reference = _bookingReferenceClient.GenerateBookingReference();
            _bookingReferenceClient.BookTrain(
                trainId.Id, 
                reference, 
                seats.Select(seat=> new SeatDto(seat)).ToList());
            return new BookingReference(reference);
        }
    }
}