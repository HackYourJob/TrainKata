import com.google.gson.Gson;
import domain.Coach;
import domain.Reservation;
import domain.Seat;
import domain.TrainId;
import infra.*;

import java.util.*;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private final GetSncfTrainTopology getSncfTrainTopology;
    private final BookSncfTrain bookSncfTrain;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        bookSncfTrain = new BookSncfTrain(bookingReferenceClient);
        this.getSncfTrainTopology =new GetSncfTrainTopology(trainDataClient);
    }

    public String makeReservation(ReservationRequestDto request) {
        List<Coach> coaches = getSncfTrainTopology.getTrainTopology(new TrainId(request.trainId));

        Coach foundCoach = tryToFindAvailableCoach(request, coaches);

        List<Seat> chosenSeats = tryToChooseSeats(request, foundCoach);

        ReservationResponseDto reservationResponseDto = tryToBookTrain(request, foundCoach, chosenSeats);

        return serializeReservation(reservationResponseDto);
    }

    private ReservationResponseDto tryToBookTrain(ReservationRequestDto request, Coach foundCoach, List<Seat> chosenSeats) {
        if (chosenSeats.isEmpty()) {
            return new ReservationResponseDto(request.trainId, Collections.EMPTY_LIST, "");
        }
        Reservation reservation = bookSncfTrain.bookTrain(new TrainId(request.trainId), chosenSeats, foundCoach);
        return new ReservationResponseDto(reservation.trainId.id, reservation.seats.stream().map(s -> new SeatDto(foundCoach.id,s.id)).collect(Collectors.toList()), reservation.bookingId);
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