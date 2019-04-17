import com.google.gson.Gson;

import java.util.*;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    public String makeReservation(ReservationRequestDto request) {
        List<Coach> coaches = getTrainTopology(request);

        Coach foundCoach = tryToFindAvailableCoach(request, coaches);

        List<Seat> chosenSeats = tryToChooseSeats(request, foundCoach);


        ReservationResponseDto reservationResponseDto = tryToBookTrain(request, chosenSeats, foundCoach);
        return serializeReservation(reservationResponseDto);
    }

    class Coach {
        private final String id;
        private final List<Seat> seats;

        public Coach(String id, List<Seat> seats) {
            this.id = id;
            this.seats = seats;
        }
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

    private List<Coach> getTrainTopology(ReservationRequestDto request) {
        String trainTopology = callSncfToGetTopology(request);

        return deserializeTopology(trainTopology).entrySet().stream()
                .map(entry -> new Coach(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Map<String, List<Seat>> deserializeTopology(String trainTopology) {
        Map<String, List<Seat>> coaches = new HashMap<>();
        for (Topologie.TopologieSeat seat : new Gson().fromJson(trainTopology, Topologie.class).seats.values()) {
            coaches.computeIfAbsent(seat.coach, _k -> new ArrayList<>()).add(new Seat(seat.seat_number, seat.booking_reference.isEmpty()));
        }
        return coaches;
    }

    private String callSncfToGetTopology(ReservationRequestDto request) {
        return trainDataClient.getTopology(request.trainId);
    }

}