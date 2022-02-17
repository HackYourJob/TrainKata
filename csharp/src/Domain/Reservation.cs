using System.Collections.Generic;

namespace TrainKata.Domain;

public struct Reservation
{
    public TrainId TrainId { get; }
    public BookingReference BookingReference { get; }
    public List<SeatId> Seats { get; }

    public Reservation(TrainId trainId, BookingReference bookingReference, List<SeatId> seats)
    {
        TrainId = trainId;
        BookingReference = bookingReference;
        Seats = seats;
    }
}