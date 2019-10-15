package infra.train_data_client;

import com.google.gson.Gson;
import domain.*;
import domain.out.GetTopologie;
import infra.TopologieDto;
import infra.TrainDataClient;

import java.util.List;
import java.util.stream.Collectors;

public class GetTopologieAdapter implements GetTopologie {

    private final TrainDataClient trainDataClient;

    public GetTopologieAdapter(TrainDataClient trainDataClient) {
        this.trainDataClient = trainDataClient;
    }

    @Override
    public Topologie getByTrainId(TrainId trainId) {
        return getTopologie(trainId);
    }

    private Topologie getTopologie(TrainId trainId) {
        String serializedTopologie = trainDataClient.getTopology(trainId.toString());
        var coachList = deserializeTopologie(serializedTopologie);
        return new Topologie(coachList);
    }

    private List<Coach> deserializeTopologie(String topologie) {
        return new Gson().fromJson(topologie, TopologieDto.class).seats.values()
                .stream()
                .collect(Collectors.groupingBy(topologieSeatDto -> topologieSeatDto.coach))
                .entrySet()
                .stream()
                .map(coach -> new Coach(
                        new CoachId(coach.getKey()),
                        coach.getValue().stream()
                                .map(dto -> new CoachSeat(
                                        new Seat(dto.coach, dto.seat_number),
                                        dto.booking_reference.isEmpty()
                                                ? SeatAvailability.AVAILABLE
                                                : SeatAvailability.BOOKED)
                                ).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

}
