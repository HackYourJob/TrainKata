using System;
using System.Linq;
using TrainKata.Domain;
using TrainKata.Domain.PortIn;

namespace TrainKata.Infra
{
    public class MakeReservationAdaptater
    {
        private readonly MakeReservation _portIn;

        public MakeReservationAdaptater(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            var bookingReferenceAdaptater = new BookingReferenceAdaptater(bookingReferenceClient);
            _portIn = new MakeReservation(
                new GetTopologieAdaptater(trainDataClient), 
                bookingReferenceAdaptater, 
                bookingReferenceAdaptater);
        }

        public String MakeReservation(ReservationRequestDto request)
        {
            var trainId = new TrainId(request.TrainId);
            var requestedSeatsNb = request.SeatCount;

            var reservationOrDefault = _portIn.Process(trainId, requestedSeatsNb);

            return SerializeResponse(request, reservationOrDefault);
        }

        private static string SerializeResponse(ReservationRequestDto request, Reservation? reservationOrDefault)
        {
            if (reservationOrDefault.HasValue)
            {
                var reservation = reservationOrDefault.Value;
                return "{" +
                       "\"train_id\": \"" + reservation.TrainId + "\", " +
                       "\"booking_reference\": \"" + reservation.BookingReference + "\", " +
                       "\"seats\": [" +
                       String.Join(", ", reservation.Seats.Select(s => "\"" + s.SeatNumber + s.CoachId + "\"")) +
                       "]" +
                       "}";
            }
            else
            {
                return "{\"train_id\": \"" + request.TrainId + "\", \"booking_reference\": \"\", \"seats\": []}";
            }
        }
    }
}
