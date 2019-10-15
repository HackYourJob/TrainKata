import java.util.Map;

// FIXME: Dans le domain ?
public class TopologieDto {
    public Map<String, TopologieSeatDto> seats;

    public class TopologieSeatDto {
        public String booking_reference;
        public int seat_number;
        public String coach;
    }
}
