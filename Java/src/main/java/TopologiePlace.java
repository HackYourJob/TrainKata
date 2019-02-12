public class TopologiePlace {
    private final PlaceId placeId;
    private final StatutPlace statutPlace;

    public TopologiePlace(PlaceId placeId, StatutPlace statutPlace) {
        this.placeId = placeId;
        this.statutPlace = statutPlace;
    }

    public PlaceId getPlaceId() {
        return placeId;
    }
}
