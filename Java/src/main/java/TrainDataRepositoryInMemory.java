public class TrainDataRepositoryInMemory implements TrainDataRepository {

    @Override
    public Topology getTopology(String trainId) {
        return new Topology();
    }
}
