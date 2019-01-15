import com.google.gson.Gson;
import domain.out.Topologie;
import domain.in.TrainReservationResult;
import domain.in.MakeReservation;
import domain.train.TrainId;
import domain.out.BookingReferenceClient;
import domain.out.TrainDataClient;
import infra.ReservationResponseSerializer;
import infra.Seat;
import org.junit.*;

import java.util.List;

public class TicketOfficeServiceTest {
    private static final String TrainId = "9043-2018-05-24";
    private static final String BookingReference = "75bcd15";

    @Test
    public void Reserve_seats_when_train_is_empty()
    { 
        int seatsRequestedCount = 3;
        MakeReservation service = buildTicketOfficeService(TrainTopologies.With_10_available_seats());
        
        String reservation = makeReservation(seatsRequestedCount, service);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1A\", \"2A\", \"3A\"]}", reservation);
    }

    private String makeReservation(int seatsRequestedCount, MakeReservation service) {
        TrainReservationResult trainReservationResult = service.execute(new TrainId(TrainId), seatsRequestedCount);
        return new ReservationResponseSerializer().serialize(trainReservationResult);
    }

    @Test
    public void Not_reserve_seats_when_not_enough_free_place()
    {
        int seatsRequestedCount = 5;
        MakeReservation trainDataService = buildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved());

        String reservation = makeReservation(seatsRequestedCount, trainDataService);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
    }

    @Test
    public void Reserve_seats_when_one_coach_is_full_and_one_is_empty()
    {
        int seatsRequestedCount = 3;
        MakeReservation trainDataService = buildTicketOfficeService(TrainTopologies.With_2_coaches_and_the_first_coach_is_full());

        String reservation = makeReservation(seatsRequestedCount, trainDataService);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1B\", \"2B\", \"3B\"]}", reservation);
    }

    @Test
    public void Reserve_all_seats_in_the_same_coach()
    {
        int seatsRequestedCount = 2;
        MakeReservation trainDataService = buildTicketOfficeService(TrainTopologies.With_2_coaches_and_9_seats_already_reserved_in_the_first_coach());

        String reservation = makeReservation(seatsRequestedCount, trainDataService);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1B\", \"2B\"]}", reservation);
    }

    @Test
    public void Cannot_Reserve_When_Train_Is_Not_Full_But_Not_Coach_Is_Available()
    {
        int seatsRequestedCount = 2;
        MakeReservation trainDataService = buildTicketOfficeService(TrainTopologies.With_10_coaches_half_available());

        String reservation = makeReservation(seatsRequestedCount, trainDataService);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
    }

    @Test
    @Ignore
    public void Not_reserve_seats_when_it_exceed_max_capacity_threshold()
    {
        int seatsRequestedCount = 3;
        MakeReservation service = buildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved());

        String reservation = makeReservation(seatsRequestedCount, service);

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
    }

    private static MakeReservation buildTicketOfficeService(String topologies)
    {
        return new MakeReservation(new TrainDataClientStub(topologies), new BookingReferenceClientStub(BookingReference));
    }

    private static class BookingReferenceClientStub implements BookingReferenceClient {
        private String bookingReference;

        public BookingReferenceClientStub(String bookingReference) {
            this.bookingReference = bookingReference;
        }

        @Override
        public String generateBookingReference(){
            return this.bookingReference;
        }

        @Override
        public void bookTrain(String trainId, String bookingReference, List<Seat> seats){
        }
    }

    private static class TrainDataClientStub implements TrainDataClient {
        private String topologies;

        public TrainDataClientStub(String topologies) {
            this.topologies = topologies;
        }

        @Override
        public Topologie getTopology(domain.train.TrainId trainId) {
            return new Gson().fromJson(topologies, Topologie.class);
        }
    }
}
