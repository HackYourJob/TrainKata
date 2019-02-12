public class TrainTopologyServiceFake implements ITrainTopologyService {
    private TrainTopologie topologie;

    public TrainTopologyServiceFake(TrainTopologie topologie) {
        this.topologie = topologie;
    }

    public TrainTopologie get(TrainId trainId) {
        return this.topologie;
    }
}
