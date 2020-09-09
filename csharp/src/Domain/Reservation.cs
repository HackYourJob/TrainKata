using System.Collections.Generic;

namespace KataTrainReservation
{
    public struct Reservation
    {
        public Reservation(TrainId trainId, BookingReference bookingReference, IEnumerable<SeatId> seats)
        {
            TrainId = trainId;
            BookingReference = bookingReference;
            Seats = seats;
        }

        public TrainId TrainId { get; }

        public BookingReference BookingReference { get; }
        
        public IEnumerable<SeatId> Seats { get; }
    }
}