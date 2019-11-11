using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace KataTrainReservation
{
    public class ReservationRequestDto
    {
        public string TrainId { get; private set; }
        public int SeatCount { get; private set; }

        public ReservationRequestDto(string trainId, int seatCount)
        {
            this.TrainId = trainId;
            this.SeatCount = seatCount;
        }
    }
}