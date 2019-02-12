public class TrainTopologyService {
    private TrainTopologie topologie;

    public TrainTopologyService(TrainTopologie topologie) {
        this.topologie = topologie;
    }

    public TrainTopologie get(TrainId trainId) {
        return this.topologie;
    }
}
