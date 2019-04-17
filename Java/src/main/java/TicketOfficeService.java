import domain.*;
import domain.ports.out.BookTrain;
import domain.ports.out.GetTrainTopology;
import infra.BookSncfTrain;
import infra.BookingReferenceClient;
import infra.GetSncfTrainTopology;
import infra.ReservationRequestDto;
import infra.ReservationResponseDto;
import infra.SeatDto;
import infra.TrainDataClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private final GetTrainTopology getTrainTopology;
    private final BookTrain bookTrain;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.bookTrain = new BookSncfTrain(bookingReferenceClient);
        this.getTrainTopology = new GetSncfTrainTopology(trainDataClient);
    }

    public Reservation makeReservation(MakeReservation makeReservation) {
        List<Coach> coaches = getTrainTopology.getTrainTopology(makeReservation.trainId);

        Coach foundCoach = tryToFindAvailableCoach(makeReservation, coaches);

        List<Seat> chosenSeats = tryToChooseSeats(makeReservation, foundCoach);

        if (chosenSeats.isEmpty()) {
            // TODO: 17/04/2019 Optionnal
            return null;
        }
        Reservation reservation = bookTrain.bookTrain(makeReservation.trainId, chosenSeats, foundCoach);
        return reservation;
    }

    public String makeReservation(ReservationRequestDto request) {
        Reservation reservation = makeReservation(new MakeReservation(new TrainId(request.trainId), request.seatCount));

        if (reservation == null) {
            return serializeReservation(
                    new ReservationResponseDto(request.trainId, Collections.EMPTY_LIST, "")
            );
        }
        List<SeatDto> seatDtos = reservation.seats.stream().map(s -> new SeatDto(reservation.coachId, s.id)).collect(Collectors.toList());
        return serializeReservation(new ReservationResponseDto(reservation.trainId.id, seatDtos, reservation.bookingId));
    }

    private ReservationResponseDto tryToBookTrain(ReservationRequestDto request, Coach foundCoach, List<Seat> chosenSeats) {
        if (chosenSeats.isEmpty()) {
            return new ReservationResponseDto(request.trainId, Collections.EMPTY_LIST, "");
        }
        Reservation reservation = bookTrain.bookTrain(new TrainId(request.trainId), chosenSeats, foundCoach);
        List<SeatDto> seatDtos = reservation.seats.stream().map(s -> new SeatDto(foundCoach.id, s.id)).collect(Collectors.toList());
        return new ReservationResponseDto(reservation.trainId.id, seatDtos, reservation.bookingId);
    }

    private String serializeReservation(ReservationResponseDto response) {

        return "{" +
                "\"train_id\": \"" + response.trainId + "\", " +
                "\"booking_reference\": \"" + response.bookingId + "\", " +
                "\"seats\": [" + response.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";

    }

    private List<Seat> tryToChooseSeats(MakeReservation command, Coach foundCoach) {
        List<Seat> chosenSeats = new ArrayList<>();
        if (foundCoach != null) {
            long limit = command.nbSeats;
            for (Seat seat : foundCoach.seats) {
                if (seat.available) {
                    if (limit-- == 0) break;
                    chosenSeats.add(seat);
                }
            }
        }
        return chosenSeats;
    }

    private Coach tryToFindAvailableCoach(MakeReservation command, List<Coach> coaches) {
        Coach foundCoach = null;
        for (Coach coach : coaches) {
            long nbAvailableSeats = 0L;
            for (Seat seat : coach.seats) {
                if (seat.available) {
                    nbAvailableSeats++;
                }
            }
            if (nbAvailableSeats >= command.nbSeats) {
                foundCoach = coach;
                break;
            }
        }
        return foundCoach;
    }

}