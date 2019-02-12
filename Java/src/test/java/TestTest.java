import org.junit.Test;

import static org.junit.Assert.*;

public class TestTest {
    @Test
    public void quandJeReserveDansUnTrainVideAlorsJaiUnePlace() {
        //Given
        TrainTopologie topologie = trainVide();
        int nbPlaces = 1;
        String trainId = "trainId";
        String idDeReservation = "IdDeReservation1";
        Reserver reserver = new Reserver(
                new TrainTopologyService(topologie),
                new ReservationReferenceService(idDeReservation),
                new ReservationService());

        //When
        Reservation reservation = reserver.execute(trainId, nbPlaces);

        //Then
        Reservation reservationAttendue = new Reservation(new PlaceId("1A"), idDeReservation);
        assertEquals(reservation, reservationAttendue);
    }

    private TrainTopologie trainVide() {
        return new TrainTopologie(
                new TopologiePlace(new PlaceId("1A"), StatutPlace.Libre),
                new TopologiePlace(new PlaceId("1B"), StatutPlace.Libre),
                new TopologiePlace(new PlaceId("1C"), StatutPlace.Libre));
    }
}