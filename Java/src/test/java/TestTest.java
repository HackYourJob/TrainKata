import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestTest {

    private static final int DEUX_PLACES = 2;
    private static final ReservationId RESERVATION_ID = new ReservationId("IdDeReservation1");
    private static final TrainId TRAIN_ID = new TrainId("trainId");
    private static final int Places = 1;

    @Test
    public void quandJeReserveDansUnTrainVideAlorsJaiUnePlace() {
        withTopologie(trainVide())
                .whenReserve(1 * Places)
                .thenCallReservationService()
                .andReturn(new Reservation(RESERVATION_ID, Arrays.asList(new PlaceId("1A"))));
    }

    @Test
    public void quandJeReserveDansUnTrainPleinJeNAiPasDePlace() {
        withTopologie(trainPlein())
                .whenReserve(1 * Places)
                .thenNotCallReservationService()
                .andReturnEmptyReservation();
    }

    @Test
    public void quandJeReserve2PlacesJObtiens1ReservationEt2PlacesSiLesPlacesSontLibres() {
        //Given
        TrainTopologie topologie = trainVide();
        TrainId trainId = new TrainId("trainId");
        ReservationId reservationId = new ReservationId("IdDeReservation1");
        ReservationServiceSpy reservationService = new ReservationServiceSpy();
        Reserver reserver = new Reserver(
                new TrainTopologyServiceFake(topologie),
                new ReservationReferenceServiceFake(reservationId),
                reservationService);

        //When
        Optional<Reservation> reservation = reserver.execute(trainId, DEUX_PLACES);

        //Then
        assertEquals(reservation.get().getPlaces().size(), 2);
        assertEquals(reservation, reservationService.getReservation());
    }

    // TODO: 12/02/19 refacto test

    @Test
    public void quandJeReserve2PlacesEtQuilEnReste1DansLePremierWagonAlorsMesPlacesSontDansLeSecondWagon() {
        //Given
        TrainTopologie topologie = trainVide();
        TrainId trainId = new TrainId("trainId");
        ReservationId reservationId = new ReservationId("IdDeReservation1");
        ReservationServiceSpy reservationService = new ReservationServiceSpy();
        Reserver reserver = new Reserver(
                new TrainTopologyServiceFake(topologie),
                new ReservationReferenceServiceFake(reservationId),
                reservationService);

        //When
        Optional<Reservation> reservation = reserver.execute(trainId, DEUX_PLACES);

        //Then
        assertEquals(reservation.get().getPlaces().size(), 2);
        assertEquals(reservation, reservationService.getReservation());
    }

    private When2 withTopologie(TrainTopologie trainTopologie) {
        return new TestBuilder(trainTopologie);
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

    private interface When2 {

        Then whenReserve(int placesNb);
    }

    private interface Then {

        Then thenCallReservationService();

        void andReturn(Reservation expectedReservation);

        Then thenNotCallReservationService();

        void andReturnEmptyReservation();
    }

    private class TestBuilder implements When2, Then {
        private final Reserver reserver;
        private final ReservationServiceSpy reservationService;
        private Optional<Reservation> reservation;

        public TestBuilder(TrainTopologie trainTopologie) {
            this.reservationService = new ReservationServiceSpy();
            this.reserver = new Reserver(
                    new TrainTopologyServiceFake(trainTopologie),
                    new ReservationReferenceServiceFake(RESERVATION_ID),
                    reservationService);
        }

        @Override
        public Then whenReserve(int placesNb) {
            this.reservation = this.reserver.execute(TRAIN_ID, placesNb);
            return this;
        }

        @Override
        public Then thenCallReservationService() {
            assertEquals(reservation, this.reservationService.getReservation());
            return this;
        }

        @Override
        public void andReturn(Reservation expectedReservation) {
            assertEquals(expectedReservation, reservation.get());
        }

        @Override
        public Then thenNotCallReservationService() {
            assertFalse(reservationService.getReservation().isPresent());

            return this;
        }

        @Override
        public void andReturnEmptyReservation() {
            assertFalse(reservation.isPresent());
        }
    }
}