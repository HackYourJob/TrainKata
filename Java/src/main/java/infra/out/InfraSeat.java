package infra.out;

public class InfraSeat {
    public final String coach;
    public final int seatNumber;

    public InfraSeat(String coach, int seatNumber) {
        this.coach = coach;
        this.seatNumber = seatNumber;
    }

    public boolean equals(Object o) {
        InfraSeat other = (InfraSeat)o;
        return coach==other.coach && seatNumber==other.seatNumber;
    }
}
