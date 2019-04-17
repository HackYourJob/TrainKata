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

        return serializeReservation(request, chosenSeats);
    }

    class Coach{
        private final String id;
        private final List<Topologie.TopologieSeat> seats;

        public Coach(String id, List<Topologie.TopologieSeat> seats) {
            this.id = id;
            this.seats = seats;
        }
    }

    private String serializeReservation(ReservationRequestDto request, List<Seat> chosenSeats) {
        if (!chosenSeats.isEmpty()) {
            ReservationResponseDto reservation = new ReservationResponseDto(request.trainId, chosenSeats, bookingReferenceClient.generateBookingReference());
            return "{" +
                    "\"train_id\": \"" + reservation.trainId + "\", " +
                    "\"booking_reference\": \"" + reservation.bookingId + "\", " +
                    "\"seats\": [" + reservation.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                    "}";
        } else {
            return "{\"train_id\": \"" + request.trainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }
    }

    private List<Seat> tryToChooseSeats(ReservationRequestDto request, Coach foundCoach) {
        //Recherche des sièges dans le wagon identifié
        List<Seat> chosenSeats = new ArrayList<>();
        if (foundCoach != null) {
            List<Seat> seats = new ArrayList<>();
            long limit = request.seatCount;
            for (Topologie.TopologieSeat seatTopology : foundCoach.seats) {
                if ("".equals(seatTopology.booking_reference)) {
                    if (limit-- == 0) break;
                    Seat seat = new Seat(seatTopology.coach, seatTopology.seat_number);
                    seats.add(seat);
                }
            }
            chosenSeats = seats;
        }
        return chosenSeats;
    }

    private Coach tryToFindAvailableCoach(ReservationRequestDto request, List<Coach> coaches) {
        Coach foundCoach = null;
        for (Coach coach : coaches) {
            long nbAvailableSeats = 0L;
            for (Topologie.TopologieSeat seat : coach.seats) {
                if ("".equals(seat.booking_reference)) {
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

    private Map<String, List<Topologie.TopologieSeat>> deserializeTopology(String trainTopology) {
        Map<String, List<Topologie.TopologieSeat>> coaches = new HashMap<>();
        for (Topologie.TopologieSeat seat : new Gson().fromJson(trainTopology, Topologie.class).seats.values()) {
            coaches.computeIfAbsent(seat.coach, _k -> new ArrayList<>()).add(seat);
        }
        return coaches;
    }

    private String callSncfToGetTopology(ReservationRequestDto request) {
        return trainDataClient.getTopology(request.trainId);
    }

}