import com.google.gson.Gson;

import com.google.gson.Gson;
import domain.Topologie;
import domain.TrainReservationFailure;
import domain.TrainReservationResult;
import domain.TrainReservationSuccess;
import domain.in.MakeReservation;
import domain.in.TrainId;
import domain.out.BookingReferenceClient;
import domain.out.TrainDataClient;
import infra.ReservationRequestDto;
import infra.Seat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    public TrainReservationResult makeReservation(ReservationRequestDto request) {
        return null;
        /*String trainData = trainDataClient.getTopology(request.trainId);

        TrainReservationResult trainReservationResult = new MakeReservation().execute(new TrainId(request.trainId), request.seatCount);

        List<Seat> seats = computeSeats(trainData, request.seatCount);

        if(seats.isEmpty()) return new TrainReservationFailure(new TrainId(request.trainId));//"{\"train_id\": \""+request.trainId+"\", \"booking_reference\": \"\", \"seats\": []}";

        return new TrainReservationSuccess(request.trainId, bookingReferenceClient.generateBookingReference(), seats);
    }

    private List<Seat> computeSeats(String trainData, int seatCount) {
        Topologie itemWithOwner = new Gson().fromJson(trainData, Topologie.class);

        return itemWithOwner.seats.values().stream()
                .collect(Collectors.groupingBy(topologieSeat -> topologieSeat.coach)).entrySet().stream()
                .filter(coach -> coach.getValue().stream()
                        .filter(s -> "".equals(s.booking_reference))
                        .count() >= seatCount)
                .findFirst()
                .map(coach -> coach.getValue().stream().filter(s -> "".equals(s.booking_reference)).limit(seatCount).map(s -> new Seat(s.coach, s.seat_number)).collect(Collectors.toList()))
                .orElse(new ArrayList<Seat>());
    }*/
    }
}