using System.Collections.Generic;

namespace KataTrainReservation
{
    public interface BookingTrain
    {
        BookingReference Book(TrainId trainId, IList<SeatId> seats);
    }
}