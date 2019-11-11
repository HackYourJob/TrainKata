using System.Collections.Generic;

namespace TrainKata
{
    public struct Reservation
    {
        public TrainId TrainId { get; }

        public BookingReference BookingReference { get; }

        public IEnumerable<SeatId> Seats { get; }

        public Reservation(TrainId trainId, BookingReference bookingReference, IEnumerable<SeatId> seats)
        {
            TrainId = trainId;
            BookingReference = bookingReference;
            Seats = seats;
        }
    }
}