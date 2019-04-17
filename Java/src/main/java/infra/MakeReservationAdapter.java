package infra;

import domain.*;
import domain.ports.in.MakeReservation;
import domain.ports.out.BookTrain;
import domain.ports.out.GetTrainTopology;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MakeReservationAdapter {

    private final MakeReservation makeReservation;

    public MakeReservationAdapter(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        BookTrain bookTrain = new BookSncfTrain(bookingReferenceClient);
        GetTrainTopology getTrainTopology = new GetSncfTrainTopology(trainDataClient);
        makeReservation = new MakeReservation(getTrainTopology, bookTrain);
    }

    public String makeReservation(ReservationRequestDto request) {
        ReservationDomainEvent reservationDomainEvent = makeReservation.makeReservation(new MakeReservationCommand(new TrainId(request.trainId), request.seatCount));

        if (reservationDomainEvent instanceof ReservationFailed) {
            return serializeReservation(
                    new ReservationResponseDto(request.trainId, Collections.EMPTY_LIST, "")
            );
        }

        ReservationSucceed reservationSucceed = (ReservationSucceed) reservationDomainEvent;
        List<SeatDto> seatDtos = reservationSucceed.seats.stream().map(s -> new SeatDto(reservationSucceed.coachId, s.id)).collect(Collectors.toList());
        return serializeReservation(new ReservationResponseDto(reservationSucceed.trainId.id, seatDtos, reservationSucceed.bookingId));
    }

    private ReservationResponseDto tryToBookTrain(ReservationRequestDto request, Coach foundCoach, List<Seat> chosenSeats) {
        if (chosenSeats.isEmpty()) {
            return new ReservationResponseDto(request.trainId, Collections.EMPTY_LIST, "");
        }
        ReservationSucceed reservation = makeReservation.bookTrain.bookTrain(new TrainId(request.trainId), chosenSeats, foundCoach);
        List<SeatDto> seatDtos = reservation.seats.stream().map(s -> new SeatDto(foundCoach.id.id, s.id)).collect(Collectors.toList());
        return new ReservationResponseDto(reservation.trainId.id, seatDtos, reservation.bookingId);
    }

    private String serializeReservation(ReservationResponseDto response) {

        return "{" +
                "\"train_id\": \"" + response.trainId + "\", " +
                "\"booking_reference\": \"" + response.bookingId + "\", " +
                "\"seats\": [" + response.seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";

    }

    private List<Seat> tryToChooseSeats(MakeReservationCommand command, Coach foundCoach) {
        return makeReservation.tryToChooseSeats(command, foundCoach);
    }

    private Coach tryToFindAvailableCoach(MakeReservationCommand command, List<Coach> coaches) {
        return makeReservation.tryToFindAvailableCoach(command, coaches);
    }

}