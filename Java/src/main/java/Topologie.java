import java.util.List;
import java.util.Optional;

public class Topologie {

    public final List<Coach> coaches;

    public Topologie(List<Coach> coaches) {
        this.coaches = coaches;
    }

    public Optional<List<Seat>> tryToGetAvailableSeats(SeatCount seatCount) {
        return coaches
                .stream()
                .flatMap(coach -> coach.tryToGetAvailableSeats(seatCount).stream())
                .findFirst();
    }
}
