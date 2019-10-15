import domain.*;
import domain.out.BookTrain;
import domain.out.GenerateBookingReference;
import domain.out.GetTopologie;
import infra.BookingReferenceClient;
import infra.ReservationRequestDto;
import infra.TrainDataClient;
import infra.booking_reference_client.BookingReferenceAdapter;
import infra.train_data_client.GetTopologieAdapter;

import java.util.Optional;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private final GetTopologie getTopologie;
    private final BookTrain bookTrain;
    private final GenerateBookingReference generateBookingReference;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.bookingReferenceClient = bookingReferenceClient;
        this.getTopologie = new GetTopologieAdapter(trainDataClient);
        this.bookTrain = new BookingReferenceAdapter(bookingReferenceClient);
        this.generateBookingReference = new BookingReferenceAdapter(bookingReferenceClient);
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
    public Optional<Reservation> makeReservation(ReservationRequest reservationRequest) {
        // FIXME : Retourner une topologie
        // FIXME: Déplacer (infra)
        Topologie topologie = getTopologie.getByTrainId(reservationRequest.trainId);


        new MakeReservation().makeReservation(reservationRequest.seatCount, topologie);

        var availableSeats = topologie.tryToGetAvailableSeats(reservationRequest.seatCount);
        if (availableSeats.isEmpty()) {
            return Optional.empty();
        }
        BookingReference bookingReference = generateBookingReference.execute();

        Reservation reservation = new Reservation(
                reservationRequest.trainId,
                bookingReference,
                availableSeats.get()
        );

        bookTrain.execute(reservation);
        return Optional.of(reservation);
    }

}