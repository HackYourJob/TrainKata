using System.Collections.Generic;

namespace KataTrainReservation
{
    public interface IBookingReferenceClient
    {
        string GenerateBookingReference();

        void BookTrain(string trainId, string bookingReference, List<SeatDto> seats);
    }
}