package domain;

import java.util.List;

public class Coach {
    public final String id;
    public final List<Seat> seats;

    public Coach(String id, List<Seat> seats) {
        this.id = id;
        this.seats = seats;
    }
}
