import com.google.gson.Gson;
import domain.*;
import infra.BookingReferenceClient;
import infra.ReservationRequestDto;
import infra.TopologieDto;
import infra.TrainDataClient;
import infra.train_data_client.GetTopologieAdapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private final GetTopologieAdapter topologieAdapter;
    private TrainDataClient trainDataClient;
    private BookingReferenceClient bookingReferenceClient;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.trainDataClient = trainDataClient;
        this.bookingReferenceClient = bookingReferenceClient;
        this.topologieAdapter = new GetTopologieAdapter(trainDataClient);
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
        Topologie topologie = topologieAdapter.getByTrainId(reservationRequest.trainId);


        new MakeReservation().makeReservation(reservationRequest.seatCount, topologie);

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


}