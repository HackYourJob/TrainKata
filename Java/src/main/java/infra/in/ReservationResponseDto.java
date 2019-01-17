package infra.in;

import java.util.List;

public class ReservationResponseDto {
	public final String trainId;
    public final String bookingId;
    public final List<Seat> seats;

    public ReservationResponseDto(String trainId, List<Seat> seats, String bookingId) {
		this.trainId = trainId;
        this.bookingId = bookingId;
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "ReservationResponseDto{" +
                "trainId='" + trainId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", seats=" + seats +
                '}';
    }

    public static class Seat {
        public final String coach;
        public final int seatNumber;

        public Seat(String coach, int seatNumber) {
            this.coach = coach;
            this.seatNumber = seatNumber;
        }

    }
}
