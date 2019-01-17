package domain;

public class Seat {
    public final String coach;
    public final int seatNumber;
    public final boolean isAvailable;

    public Seat(String coach, int seatNumber, String bookingReference) {
        this.coach = coach;
        this.seatNumber = seatNumber;
        this.isAvailable = "".equals(bookingReference);
    }
}
