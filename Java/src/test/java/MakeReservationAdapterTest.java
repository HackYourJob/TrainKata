import infra.*;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class MakeReservationAdapterTest {
    private static final String TrainId = "9043-2018-05-24";
    private static final String BookingReference = "75bcd15";
    private static BookingReferenceClientStub bookingReferenceClient;
    private static TrainDataClientStub trainDataClient;

    @Test
    public void Reserve_seats_when_train_is_empty()
    { 
        int seatsRequestedCount = 3;
        MakeReservationAdapter service = buildTicketOfficeService(TrainTopologies.With_10_available_seats());
        
        String reservation = service.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1A\", \"2A\", \"3A\"]}", reservation);
    }

    @Test
    public void Call_service_when_seats_are_available()
    {
        int seatsRequestedCount = 3;
        MakeReservationAdapter service = buildTicketOfficeService(TrainTopologies.With_10_available_seats());

        String reservation = service.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals(TrainId, bookingReferenceClient.trainIdSubmitted);
        Assert.assertEquals(BookingReference, bookingReferenceClient.bookingReferenceSubmitted);
        Assert.assertArrayEquals(new String[] { "1A", "2A", "3A" }, bookingReferenceClient.seatsSubmitted.stream().map(seat -> seat.seatNumber + seat.coach).toArray());
    }

    @Test
    public void Not_reserve_seats_when_not_enough_free_place()
    {
        int seatsRequestedCount = 5;
        MakeReservationAdapter trainDataService = buildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved());

        String reservation = trainDataService.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
    }

    @Test
    public void Reserve_seats_when_one_coach_is_full_and_one_is_empty()
    {
        int seatsRequestedCount = 3;
        MakeReservationAdapter trainDataService = buildTicketOfficeService(TrainTopologies.With_2_coaches_and_the_first_coach_is_full());

        String reservation = trainDataService.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1B\", \"2B\", \"3B\"]}", reservation);
    }

    @Test
    public void Reserve_all_seats_in_the_same_coach()
    {
        int seatsRequestedCount = 2;
        MakeReservationAdapter trainDataService = buildTicketOfficeService(TrainTopologies.With_2_coaches_and_9_seats_already_reserved_in_the_first_coach());

        String reservation = trainDataService.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \""+ BookingReference +"\", \"seats\": [\"1B\", \"2B\"]}", reservation);
    }

    @Test
    public void Cannot_Reserve_When_Train_Is_Not_Full_But_Not_Coach_Is_Available()
    {
        int seatsRequestedCount = 2;
        MakeReservationAdapter trainDataService = buildTicketOfficeService(TrainTopologies.With_10_coaches_half_available());

        String reservation = trainDataService.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
    }

    @Test
    @Ignore
    public void Not_reserve_seats_when_it_exceed_max_capacity_threshold()
    {
        int seatsRequestedCount = 3;
        MakeReservationAdapter service = buildTicketOfficeService(TrainTopologies.With_10_seats_and_6_already_reserved());

        String reservation = service.makeReservation(new ReservationRequestDto(TrainId, seatsRequestedCount));

        Assert.assertEquals("{\"train_id\": \"" + TrainId + "\", \"booking_reference\": \"\", \"seats\": []}", reservation);
    }

    private static MakeReservationAdapter buildTicketOfficeService(String topologies)
    {
        bookingReferenceClient = new BookingReferenceClientStub(BookingReference);
        trainDataClient = new TrainDataClientStub(topologies);
        return new MakeReservationAdapter(trainDataClient, bookingReferenceClient);
    }

    private static class BookingReferenceClientStub implements BookingReferenceClient {
        private String bookingReference;
        public String trainIdSubmitted;
        public String bookingReferenceSubmitted;
        public List<SeatDto> seatsSubmitted;

        public BookingReferenceClientStub(String bookingReference) {
            this.bookingReference = bookingReference;
        }

        @Override
        public String generateBookingReference(){
            return this.bookingReference;
        }

        @Override
        public void bookTrain(String trainId, String bookingReference, List<SeatDto> seats){
            trainIdSubmitted = trainId;
            bookingReferenceSubmitted = bookingReference;
            seatsSubmitted = seats;
        }
    }

    private static class TrainDataClientStub implements TrainDataClient {
        private String topologies;

        public TrainDataClientStub(String topologies) {
            this.topologies = topologies;
        }

        @Override
        public String getTopology(String trainId) {
            return this.topologies;
        }
    }
}
