namespace TrainKata
{
    public class Seat
    {
        public string Coach { get; }
        public int SeatNumber { get; }

        public Seat(string coach, int seatNumber)
        {
            Coach = coach;
            SeatNumber = seatNumber;
        }

        private bool Equals(Seat other)
        {
            return Coach == other.Coach && SeatNumber == other.SeatNumber;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((Seat) obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                return ((Coach != null ? Coach.GetHashCode() : 0) * 397) ^ SeatNumber;
            }
        }
    }
}
