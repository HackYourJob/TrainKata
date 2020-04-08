using System.Collections.Generic;
using TrainKata.Domain;

namespace TrainKata
{
    public struct Reservation
    {
        public TrainId TrainId { get; }

        public IEnumerable<SeatId> Seats { get; }

        public string BookingId { get; }

        public Reservation(TrainId trainId, IList<SeatId> seats, string bookingId)
        {
            TrainId = trainId;
            Seats = seats;
            BookingId = bookingId;
        }
    }
}