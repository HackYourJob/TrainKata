using System.Collections.Generic;

namespace KataTrainReservation
{
    public class TopologieDto
    {
        public Dictionary<string, TopologieSeatDto> seats;

        public class TopologieSeatDto
        {
            public string booking_reference;
            public int seat_number;
            public string coach;
            
            public Seat ToSeat()
            {
                return new Seat(new SeatId(seat_number, coach), IsSeatAvailable());
            }
            
            private bool IsSeatAvailable()
            {
                return "".Equals(booking_reference);
            }
        }
    }
}