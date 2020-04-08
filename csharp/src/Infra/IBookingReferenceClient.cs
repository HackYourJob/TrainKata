using System.Collections.Generic;

namespace TrainKata.Infra
{
    public interface IBookingReferenceClient
    {
        string GenerateBookingReference();

        void BookTrain(string trainId, string bookingReference, List<SeatDto> seats);
    }
}