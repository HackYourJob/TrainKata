namespace KataTrainReservation
{

        public struct Seat
        {
            public Seat(SeatId id, bool isAvailable)
            {
                Id = id;
                IsAvailable = isAvailable;
            }

            public SeatId Id { get; }
            public bool IsAvailable { get; }
        }
    }