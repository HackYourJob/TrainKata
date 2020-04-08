﻿using System.Collections.Generic;

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
        }
    }
}