using System.Collections.Generic;

namespace TrainKata.Domain
{
    public struct Reservation
    {
        public TrainId TrainId { get; }

        public BookingReference BookingReference { get; }

        public IEnumerable<SeatId> Seats { get; }

        public Reservation(TrainId trainId, BookingReference bookingReference, ICollection<SeatId> seats)
        {
            TrainId = trainId;
            BookingReference = bookingReference;
            Seats = seats;
        }
    }
}