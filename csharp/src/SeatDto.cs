﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace KataTrainReservation
{
    public class SeatDto
    {
        public string Coach { get; private set; }
        public int SeatNumber { get; private set; }

        public SeatDto(string coach, int seatNumber)
        {
            this.Coach = coach;
            this.SeatNumber = seatNumber;
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }

        /// <summary>
        /// N.B. this is not how you would override equals in a production environment. :)
        /// </summary>
        public override bool Equals(object obj)
        {
            SeatDto other = obj as SeatDto;

            return this.Coach == other.Coach && this.SeatNumber == other.SeatNumber;
        }
    }
}
