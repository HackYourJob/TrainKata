package domain.train;

import domain.out.Topologie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Train {
    private List<Coach> coaches = new ArrayList<>();

    public Train(Topologie topologie) {
        Collection<List<Topologie.TopologieSeat>> rawCoaches = topologie.seats.values().stream()
                .collect(Collectors.groupingBy(topologieSeat -> topologieSeat.coach)).values();

        rawCoaches.forEach(c -> this.coaches.add(new Coach(c, c.get(0).coach)));
    }

    public Optional<List<Seat>> findAvailableSeats(int seatCount) {
        return coaches.stream().map(c -> c.tryToReserve(seatCount))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());
    }
}
