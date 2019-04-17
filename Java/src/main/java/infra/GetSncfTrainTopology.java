package infra;

import com.google.gson.Gson;
import domain.Coach;
import domain.GetTrainTopology;
import domain.Seat;
import domain.TrainId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetSncfTrainTopology implements GetTrainTopology {

    private TrainDataClient trainDataClient;

    public GetSncfTrainTopology(TrainDataClient trainDataClient) {
        this.trainDataClient = trainDataClient;
    }

    @Override
    public List<Coach> getTrainTopology(TrainId trainId) {
        String trainTopology = callSncfToGetTopology(trainId);

        return deserializeTopology(trainTopology).entrySet().stream()
                .map(entry -> new Coach(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Map<String, List<Seat>> deserializeTopology(String trainTopology) {
        Map<String, List<Seat>> coaches = new HashMap<>();
        for (Topologie.TopologieSeat seat : new Gson().fromJson(trainTopology, Topologie.class).seats.values()) {
            coaches.computeIfAbsent(seat.coach, _k -> new ArrayList<>()).add(new Seat(seat.seat_number, seat.booking_reference.isEmpty()));
        }
        return coaches;
    }

    private String callSncfToGetTopology(TrainId trainId) {
        return trainDataClient.getTopology(trainId.id);
    }
}