using System;
using System.Linq;
using TrainKata.Domain;
using TrainKata.Domain.Options;
using TrainKata.Domain.PortIn;
using TrainKata.Infra.BookingReferenceClient;
using TrainKata.Infra.TrainDataClient;

namespace TrainKata.Infra
{
    public class TicketOfficeService
    {
        private readonly MakeReservation _hexagon;

        public TicketOfficeService(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            var bookingReferenceClientAdaptater = new BookingReferenceClientAdaptater(bookingReferenceClient);
            _hexagon = new MakeReservation(new TrainDataClientAdaptater(trainDataClient), bookingReferenceClientAdaptater, bookingReferenceClientAdaptater);
        }

        public string MakeReservation(ReservationRequestDto requestDto)
        {
            return
                _hexagon.Execute(requestDto.ToRequest())
                .Map(reservation => Maybe.Some(FormatReservation(reservation)))
                .OrDefault(FormatFailedReservation(requestDto));
        }
        
        private static string FormatReservation(Reservation reservation)
        {
            return "{" +
                   "\"train_id\": \"" + reservation.TrainId + "\", " +
                   "\"booking_reference\": \"" + reservation.BookingReference + "\", " +
                   "\"seats\": [" + String.Join(", ", reservation.Seats.Select(id => "\"" + id + "\"")) + "]" +
                   "}";
        }

        private static string FormatFailedReservation(ReservationRequestDto request)
        {
            return "{\"train_id\": \"" + request.TrainId + "\", \"booking_reference\": \"\", \"seats\": []}";
        }
    }
}
