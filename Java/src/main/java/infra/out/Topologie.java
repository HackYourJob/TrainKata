package infra.out;

import java.util.Map;

import domain.Seat;

public class Topologie {
    public Map<String, TopologieSeat> seats;

    public static class TopologieSeat {
        public String booking_reference;
        public int seat_number;
        public String coach;

        public TopologieSeat(Seat seat) {
            this.booking_reference= seat.isAvailable ? "" : "reserved";
            this.coach = seat.coach;
            this.seat_number = seat.seatNumber;
        }

        public String getCoach() {
            return coach;
        }

        public boolean isAvailable() {
            return booking_reference == null || "".equals(booking_reference);
        }
    }
}
