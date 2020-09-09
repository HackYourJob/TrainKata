namespace KataTrainReservation
{
 
        public struct SeatId
        {
            public SeatId(int seatNumber, string coachNumber)
            {
                SeatNumber = seatNumber;
                CoachNumber = coachNumber;
            }

            public int SeatNumber { get; }
            public string CoachNumber { get; }
        }
    }