package domain;

import com.google.gson.Gson;
import domain.out.Topology;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Train {

    List<Coach> coaches;
    private String topology;

    public Train(String topology) {
        this.topology = topology;
    }


    public List<Seat> findAvailableSeats(int seatCount) {
        Topologie itemWithOwner = new Gson().fromJson(topology, Topologie.class);

        return itemWithOwner.seats.values().stream()
                .collect(Collectors.groupingBy(topologieSeat -> topologieSeat.coach)).entrySet().stream()
                .filter(coach -> coach.getValue().stream()
                        .filter(s -> "".equals(s.booking_reference))
                        .count() >= seatCount)
                .findFirst()
                .map(coach -> coach.getValue().stream().filter(s -> "".equals(s.booking_reference)).limit(seatCount).map(s -> new Seat(s.coach, s.seat_number)).collect(Collectors.toList()))
                .orElse(new ArrayList<Seat>());
    }
}
