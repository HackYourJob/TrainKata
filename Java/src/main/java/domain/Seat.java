package domain;

public class Seat {
    public final String coach;
    public final SeatId seatNumber;

    public Seat(String coach, SeatId seatNumber) {
        this.coach = coach;
        this.seatNumber = seatNumber;
    }

    public boolean equals(Object o) {
        Seat other = (Seat)o;
        return coach==other.coach && seatNumber==other.seatNumber;
    }
}