package domain;

import java.util.List;

public class Coach {

    private final static double MAX_THRESHOLD = 0.7;

    public final CoachId id;
    public final List<Seat> seats;

    public Coach(CoachId coachId, List<Seat> seats) {
        this.id = coachId;
        this.seats = seats;
    }

    private long getAvailableSeats() {
        long nbAvailableSeats = 0L;

        for (Seat seat : seats) {
            if (seat.available) {
                nbAvailableSeats++;
            }
        }

        return nbAvailableSeats;
    }

    private long getSeatsCount() {
        return seats.size();
    }

    public boolean canAcceptMorePeople(int nbSeats) {
        long occupiedSeats = getSeatsCount() - getAvailableSeats() + nbSeats;
        return occupiedSeats < MAX_THRESHOLD * getSeatsCount();
    }
}
