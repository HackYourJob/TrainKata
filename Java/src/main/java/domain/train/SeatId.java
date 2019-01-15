package domain.train;

public class SeatId {
    public int seat_number;
    private final String coachId;


    public SeatId(int seat_number, String coachId) {
        this.seat_number = seat_number;
        this.coachId = coachId;
    }

}
