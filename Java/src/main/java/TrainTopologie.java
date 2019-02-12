import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrainTopologie {
    private List<TopologiePlace> topologiePlaces;

    public TrainTopologie(TopologiePlace... topologiePlaces) {
        this.topologiePlaces = Arrays.asList(topologiePlaces);
    }

    public List<TopologiePlace> getPlaces() {
        return topologiePlaces;
    }

    public List<TopologiePlace> getPlacesLibres() {
        return topologiePlaces.stream()
                .filter(place -> place.getStatut() == StatutPlace.Libre)
                .collect(Collectors.toList());
    }
}
