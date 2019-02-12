import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class TestTest {
    @Test
    public void quandJeReserveDansUnTrainVideAlorsJaiUnePlace() {
        //Given
        TrainTopologie topologie = trainVide();
        int nbPlaces = 1;
        TrainId trainId = new TrainId("trainId");
        ReservationId reservationId = new ReservationId("IdDeReservation1");
        ReservationServiceSpy reservationService = new ReservationServiceSpy();
        Reserver reserver = new Reserver(
                new TrainTopologyServiceFake(topologie),
                new ReservationReferenceServiceFake(reservationId),
                reservationService);

        //When
        Optional<Reservation> reservation = reserver.execute(trainId, nbPlaces);

        //Then
        Reservation reservationAttendue = new Reservation(new PlaceId("1A"), reservationId);
        assertEquals(Optional.of(reservationAttendue), reservation);
        assertEquals(reservation, reservationService.getReservation());
    }

    @Test
    public void quandJeReserveDansUnTrainPleinJeNAiPasDePlace() {
        //Given
        TrainTopologie topologie = trainPlein();
        int nbPlaces = 1;
        TrainId trainId = new TrainId("trainId");
        ReservationId reservationId = new ReservationId("IdDeReservation1");
        ReservationServiceSpy reservationService = new ReservationServiceSpy();
        Reserver reserver = new Reserver(
                new TrainTopologyServiceFake(topologie),
                new ReservationReferenceServiceFake(reservationId),
                reservationService);

        //When
        Optional<Reservation> reservation = reserver.execute(trainId, nbPlaces);

        //Then
        assertFalse(reservation.isPresent());
        assertEquals(reservation, reservationService.getReservation());
    }

    private TrainTopologie trainPlein() {
        return new TrainTopologie(
                new TopologiePlace(new PlaceId("1A"), StatutPlace.Occupe),
                new TopologiePlace(new PlaceId("1B"), StatutPlace.Occupe),
                new TopologiePlace(new PlaceId("1C"), StatutPlace.Occupe));

    }

    private TrainTopologie trainVide() {
        return new TrainTopologie(
                new TopologiePlace(new PlaceId("1A"), StatutPlace.Libre),
                new TopologiePlace(new PlaceId("1B"), StatutPlace.Libre),
                new TopologiePlace(new PlaceId("1C"), StatutPlace.Libre));
    }
}