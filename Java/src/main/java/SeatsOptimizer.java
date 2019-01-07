import java.util.List;

public class SeatsOptimizer {


    private TrainDataRepository trainDataRepository;

    public SeatsOptimizer(TrainDataRepository trainDataRepository) {
        this.trainDataRepository = trainDataRepository;
    }

    List<Seat> getAvailableSeats(String trainId, int seatCount) {

        Topology topology = trainDataRepository.getTopology(trainId);

        return null;
    }
}
