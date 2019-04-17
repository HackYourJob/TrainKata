
public class SeatDto {
    public final String coach;
    public final int seatNumber;

    public SeatDto(String coach, int seatNumber) {
        this.coach = coach;
        this.seatNumber = seatNumber;
    }

    public boolean equals(Object o) {
        SeatDto other = (SeatDto)o;
        return coach==other.coach && seatNumber==other.seatNumber;
    }
}