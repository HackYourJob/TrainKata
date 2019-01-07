import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SeatsOptimizerTest {


    @Test
    public void shouldReturnFirstSeatsOfCoachWhenCoachHasNoReservation() {
        // Given
        TrainDataRepository trainDataRepository = new TrainDataRepository() {
            @Override
            public Topology getTopology(String trainId) {
                return new Topology();
            }
        };
        SeatsOptimizer seatsOptimizer = new SeatsOptimizer(trainDataRepository);

        // When
        List<Seat> seats = seatsOptimizer.getAvailableSeats("trainId", 3);
        // Then
        assertEquals(seats.size(), 3);

    }
}