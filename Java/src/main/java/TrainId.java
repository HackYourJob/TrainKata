import java.util.Objects;

public class TrainId {
    private String trainId;

    public TrainId(String trainId) {
        this.trainId = trainId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainId trainId1 = (TrainId) o;
        return Objects.equals(trainId, trainId1.trainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainId);
    }
}
