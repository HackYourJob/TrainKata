import java.util.Objects;

public class PlaceId {

    private String id;

    public PlaceId(String id) {

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaceId placeId = (PlaceId) o;
        return Objects.equals(id, placeId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
