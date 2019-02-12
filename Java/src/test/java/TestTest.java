import org.junit.Test;

import static org.junit.Assert.*;

public class TestTest {
    @Test
    public void quandJeReserveDansUnTrainVideAlorsJaiUnePlace() {
        //Given
        TrainTopologie topologie = trainVide();
        int nbPlaces = 1;
        TrainId trainId = new TrainId("trainId");
        ReservationId reservationId = new ReservationId("IdDeReservation1");
        Reserver reserver = new Reserver(
                new TrainTopologyServiceFake(topologie),
                new ReservationReferenceServiceFake(reservationId),
                new ReservationServiceSpy());

        //When
        Reservation reservation = reserver.execute(trainId, nbPlaces);

        //Then
        Reservation reservationAttendue = new Reservation(new PlaceId("1A"), reservationId);
        assertEquals(reservation, reservationAttendue);
    }

    private TrainTopologie trainVide() {
        return new TrainTopologie(
                new TopologiePlace(new PlaceId("1A"), StatutPlace.Libre),
                new TopologiePlace(new PlaceId("1B"), StatutPlace.Libre),
                new TopologiePlace(new PlaceId("1C"), StatutPlace.Libre));
    }
}