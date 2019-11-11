using System.Collections.Generic;

namespace TrainKata
{
    public interface IBookingReferenceClient
    {
        string GenerateBookingReference();

        void BookTrain(string trainId, string bookingReference, List<Seat> seats);
    }
}