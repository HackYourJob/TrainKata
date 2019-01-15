package domain;

public class Seat {
    public final String coach;
    public final int seatNumber;

    public Seat(String coach, int seatNumber) {
        this.coach = coach;
        this.seatNumber = seatNumber;
    }

    public int sorter() {
        return seatNumber;
    }

    public boolean equals(Object o) {
        Seat other = (Seat)o;
        return coach==other.coach && seatNumber==other.seatNumber;
    }
}