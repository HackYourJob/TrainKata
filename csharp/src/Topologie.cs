using System.Collections.Generic;

namespace KataTrainReservation
{
    public class Topologie
    {
        public Dictionary<string, TopologieSeat> seats;

        public class TopologieSeat
        {
            public string booking_reference;
            public int seat_number;
            public string coach;
        }
    }
}