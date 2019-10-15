package infra;

import domain.*;
import domain.in.MakeReservation;
import infra.BookingReferenceClient;
import infra.ReservationRequestDto;
import infra.TrainDataClient;
import infra.booking_reference_client.BookingReferenceAdapter;
import infra.train_data_client.GetTopologieAdapter;

import java.util.Optional;
import java.util.stream.Collectors;

public class TicketOfficeService {

    private final MakeReservation makeReservation;

    public TicketOfficeService(TrainDataClient trainDataClient, BookingReferenceClient bookingReferenceClient) {
        this.makeReservation = new MakeReservation(
                new GetTopologieAdapter(trainDataClient),
                new BookingReferenceAdapter(bookingReferenceClient),
                new BookingReferenceAdapter(bookingReferenceClient)
        );
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

    public Optional<Reservation> makeReservation(ReservationRequest reservationRequest) {
        return makeReservation.execute(reservationRequest);
    }

}