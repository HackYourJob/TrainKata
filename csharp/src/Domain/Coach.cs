using System.Collections.Generic;

namespace TrainKata.Domain;

public struct Coach
{
    public List<SeatId> FreeSeats { get; }

    public Coach(List<SeatId> freeSeats)
    {
        FreeSeats = freeSeats;
    }
}