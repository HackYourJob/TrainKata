import com.google.gson.Gson;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
    }

    // FIXME: move to infra
    public String makeReservation(ReservationRequestDto reservationRequest) {
        Optional<Reservation> reservation = makeReservation(reservationRequest.toDomainModel());

        if (reservation.isEmpty()) {
            return "{\"train_id\": \"" + reservationRequest.trainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }
        return "{" +
                "\"train_id\": \"" + reservation.get().trainId + "\", " +
                "\"booking_reference\": \"" + reservation.get().bookingReference.reference + "\", " +
                "\"seats\": [" + reservation.get().seats.stream().map(s -> "\"" + s.seatNumber + s.coach + "\"").collect(Collectors.joining(", ")) + "]" +
                "}";
    }

    // FIXME : Trop grosse !
    // FIXME : Retourner une réservation
    public Optional<Reservation> makeReservation(ReservationRequest reservationRequest) {
        // FIXME : Retourner une topologie
        String serializedTopologie = trainDataClient.getTopology(reservationRequest.trainId.toString());

        // FIXME: Déplacer (infra)
        var coachList = deserializeTopologie(serializedTopologie);
        var topologie = new Topologie(coachList);

        var availableSeats = topologie.tryToGetAvailableSeats(reservationRequest.seatCount);
        if (availableSeats.isEmpty()) {
            return Optional.empty();
        }
        BookingReference bookingReference = generateBookingReference();

        Reservation reservation = new Reservation(
                reservationRequest.trainId,
                bookingReference,
                availableSeats.get()
        );

        bookTrain(reservation);
        return Optional.of(reservation);
    }

    private void bookTrain(Reservation reservation) {
        this.bookingReferenceClient.bookTrain(reservation.trainId.toString(), reservation.bookingReference.reference, reservation.seats);
    }

    private BookingReference generateBookingReference() {
        return new BookingReference(bookingReferenceClient.generateBookingReference());
    }


    private List<Coach> deserializeTopologie(String topologie) {
        return new Gson().fromJson(topologie, TopologieDto.class).seats.values()
                .stream()
                .collect(Collectors.groupingBy(topologieSeatDto -> topologieSeatDto.coach))
                .entrySet()
                .stream()
                .map(coach -> new Coach(
                        new CoachId(coach.getKey()),
                        coach.getValue().stream()
                                .map(dto -> new CoachSeat(
                                        new Seat(dto.coach, dto.seat_number),
                                        dto.booking_reference.isEmpty()
                                                ? SeatAvailability.AVAILABLE
                                                : SeatAvailability.BOOKED)
                                ).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}