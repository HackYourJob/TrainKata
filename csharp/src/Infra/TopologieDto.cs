using System.Collections.Generic;
using TrainKata.Domain;

namespace TrainKata.Infra
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
                return new Seat(
                    new SeatId(seat_number, coach),
                    booking_reference == "" 
                        ? Seat.SeatStatus.Available 
                        : Seat.SeatStatus.Reserved);
            }
        }
    }
}