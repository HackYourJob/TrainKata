using TrainKata.Domain;

namespace TrainKata.Infra
{
    public class SeatDto
    {
        public string Coach { get; }
        public int SeatNumber { get; }

        public SeatDto(string coach, int seatNumber)
        {
            Coach = coach;
            SeatNumber = seatNumber;
        }

        public SeatDto(SeatId id)
        {
            Coach = id.CoachId;
            SeatNumber = id.Number;
        }

        private bool Equals(SeatDto other)
        {
            return Coach == other.Coach && SeatNumber == other.SeatNumber;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((SeatDto) obj);
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
