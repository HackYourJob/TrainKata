package external.trainData;

import java.util.Map;

public class Topologie {
    public Map<String, TopologieSeat> seats;

    public class TopologieSeat {
        public String booking_reference;
        public int seat_number;
        public String coach;

        public String getCoach() {
            return coach;
        }

        public boolean isAvailable() {
            return booking_reference == null || "".equals(booking_reference);
        }
    }
}
