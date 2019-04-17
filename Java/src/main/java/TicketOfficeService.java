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
        Map<String, List<Topologie.TopologieSeat>> coaches = getTrainTopology(request);

        Map.Entry<String, List<Topologie.TopologieSeat>> foundCoach = tryToFindAvailableCoach(request, coaches);

        List<Seat> chosenSeats = tryToChooseSeats(request, foundCoach);

        return serializeReservation(request, chosenSeats);
    }

    private String serializeReservation(ReservationRequestDto request, List<Seat> chosenSeats) {
        //Création de la réponse
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

    private List<Seat> tryToChooseSeats(ReservationRequestDto request, Map.Entry<String, List<Topologie.TopologieSeat>> foundCoach) {
        //Recherche des sièges dans le wagon identifié
        List<Seat> chosenSeats = new ArrayList<>();
        if (foundCoach != null) {
            List<Seat> seats = new ArrayList<>();
            long limit = request.seatCount;
            for (Topologie.TopologieSeat seatTopology : foundCoach.getValue()) {
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

    private Map.Entry<String, List<Topologie.TopologieSeat>> tryToFindAvailableCoach(ReservationRequestDto request, Map<String, List<Topologie.TopologieSeat>> coaches) {
        Map.Entry<String, List<Topologie.TopologieSeat>> foundCoach = null;
        for (Map.Entry<String, List<Topologie.TopologieSeat>> coach : coaches.entrySet()) {
            long nbAvailableSeats = 0L;
            for (Topologie.TopologieSeat seat : coach.getValue()) {
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

    private Map<String, List<Topologie.TopologieSeat>> getTrainTopology(ReservationRequestDto request) {
        String trainTopology = callSncfToGetTopology(request);

        return deserializeTopology(trainTopology);
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