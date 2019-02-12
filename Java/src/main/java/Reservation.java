import java.util.Objects;

public class Reservation {
    private final PlaceId placeId;
    private final String idDeReservation;

    public Reservation(PlaceId placeId, String idDeReservation) {
        this.placeId = placeId;
        this.idDeReservation = idDeReservation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(placeId, that.placeId) &&
                Objects.equals(idDeReservation, that.idDeReservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId, idDeReservation);
    }
}
