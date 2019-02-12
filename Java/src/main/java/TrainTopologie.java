import java.util.Arrays;
import java.util.List;

public class TrainTopologie {
    private List<TopologiePlace> topologiePlaces;

    public TrainTopologie(TopologiePlace... topologiePlaces) {
        this.topologiePlaces = Arrays.asList(topologiePlaces);
    }

    public List<TopologiePlace> getPlaces() {
        return topologiePlaces;
    }
}
