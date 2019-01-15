package domain;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Train {

    List<Coach> coaches = new ArrayList<>();
    private Topologie topologie;

    public Train(Topologie topologie) {
        this.topologie = topologie;
        Collection<List<Topologie.TopologieSeat>> rawCoaches = topologie.seats.values().stream()
                .collect(Collectors.groupingBy(topologieSeat -> topologieSeat.coach)).values();
        rawCoaches.forEach(c -> this.coaches.add(new Coach(c)));
    }

    public Optional<List<Seat>> findAvailableSeats(int seatCount) {

        //TODO return Either
        return coaches.stream().map(c -> c.tryToReserve(seatCount))
                .filter(Optional::isPresent)
                .findFirst()
                .orElse(Optional.empty());


        /*return topologie.seats.values().stream()
                .collect(Collectors.groupingBy(topologieSeat -> topologieSeat.coach)).entrySet().stream()
                .filter(coach -> coach.getValue().stream()
                        .filter(s -> "".equals(s.booking_reference))
                        .count() >= seatCount)
                .findFirst()
                .map(coach -> coach.getValue().stream().filter(s -> "".equals(s.booking_reference)).limit(seatCount).map(s -> new Seat(s.coach, s.seat_number)).collect(Collectors.toList()))
                .orElse(new ArrayList<Seat>());*/
    }
}
