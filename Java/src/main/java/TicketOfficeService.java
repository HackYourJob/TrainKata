import com.google.gson.Gson;
import domain.Coach;
import domain.Seat;
import domain.TrainId;
import infra.*;

import java.util.*;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private final GetSncfTrainTopology getSncfTrainTopology;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.bookingReferenceClient = bookingReferenceClient;
        this.getSncfTrainTopology =new GetSncfTrainTopology(trainDataClient);
    }

    public String makeReservation(ReservationRequestDto request) {
        List<Coach> coaches = getSncfTrainTopology.getTrainTopology(new TrainId(request.trainId));

        Coach foundCoach = tryToFindAvailableCoach(request, coaches);

        List<Seat> chosenSeats = tryToChooseSeats(request, foundCoach);


        ReservationResponseDto reservationResponseDto = tryToBookTrain(request, chosenSeats, foundCoach);
        return serializeReservation(reservationResponseDto);
    }

    private ReservationResponseDto tryToBookTrain(ReservationRequestDto request, List<Seat> chosenSeats, Coach coach) {
        if (!chosenSeats.isEmpty()) {
            ReservationResponseDto reservation = new ReservationResponseDto(
                    request.trainId,
                    chosenSeats.stream().map(s -> new SeatDto(coach.id, s.id)).collect(Collectors.toList()),
                    bookingReferenceClient.generateBookingReference());
            bookingReferenceClient.bookTrain(reservation.trainId, reservation.bookingId, reservation.seats);
            return reservation;
        }

        return new ReservationResponseDto(request.trainId, Collections.EMPTY_LIST, "");
    }

    private String serializeReservation(ReservationResponseDto response) {

        return "{" +
                "\"train_id\": \"" + response.trainId + "\", " +
                "\"booking_reference\": \"" + response.bookingId + "\", " +
                "\"seats\": [" + response.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";

    }

    private List<Seat> tryToChooseSeats(ReservationRequestDto request, Coach foundCoach) {
        List<Seat> chosenSeats = new ArrayList<>();
        if (foundCoach != null) {
            long limit = request.seatCount;
            for (Seat seat : foundCoach.seats) {
                if (seat.available) {
                    if (limit-- == 0) break;
                    chosenSeats.add(seat);
                }
            }
        }
        return chosenSeats;
    }

    private Coach tryToFindAvailableCoach(ReservationRequestDto request, List<Coach> coaches) {
        Coach foundCoach = null;
        for (Coach coach : coaches) {
            long nbAvailableSeats = 0L;
            for (Seat seat : coach.seats) {
                if (seat.available) {
                    nbAvailableSeats++;
                }
            }
            if (nbAvailableSeats >= request.seatCount) {
                foundCoach = coach;
                break;
            }
        }
        return foundCoach;
    }

}