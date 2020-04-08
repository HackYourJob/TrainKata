using System.Collections.Generic;
using TrainKata.Domain;

namespace TrainKata
{
    public interface BookReservation
    {
        Reservation Book(TrainId trainId, List<Seat> availableSeats);
    }
}