package domain;

public class SeatId {
    public int seat_number;
    private final String coachId;


    public SeatId(int seat_number, String coachId) {
        this.seat_number = seat_number;
        this.coachId = coachId;
    }

    public String getSeatId() {
        return coachId + seat_number;
    }
}
