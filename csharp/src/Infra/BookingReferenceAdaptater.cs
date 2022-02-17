using System.Linq;
using TrainKata.Domain;
using TrainKata.Domain.PortOut;

namespace TrainKata.Infra;

public class BookingReferenceAdaptater : GenerateBookingReference, ConfirmReservation
{
    private readonly IBookingReferenceClient _bookingReferenceClient;

    public BookingReferenceAdaptater(IBookingReferenceClient bookingReferenceClient)
    {
        _bookingReferenceClient = bookingReferenceClient;
    }

    public BookingReference Execute()
    {
        return new BookingReference(_bookingReferenceClient.GenerateBookingReference());
    }

    public void Execute(Reservation reservation)
    {
        _bookingReferenceClient.BookTrain(
            reservation.TrainId.ToString(), 
            reservation.BookingReference.Value,
            reservation.Seats.Select(s => new SeatDto(s)).ToList());
    }
}