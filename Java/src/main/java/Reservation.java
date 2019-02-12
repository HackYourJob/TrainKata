import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Reservation {
    private final List<PlaceId> placeIds;
    private final ReservationId idDeReservation;

    public Reservation(ReservationId reservationId, List<PlaceId> placeIds) {
        this.placeIds = placeIds;
        this.idDeReservation = reservationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(placeIds, that.placeIds) &&
                Objects.equals(idDeReservation, that.idDeReservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeIds, idDeReservation);
    }

    public List<PlaceId> getPlaces() {
        return placeIds;
    }
}
