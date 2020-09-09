using System;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace KataTrainReservation
{
    public class MakeReservationAdapter
    {
        private readonly MakeReservation _hexagon;

        public MakeReservationAdapter(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            _hexagon = new MakeReservation(new GetTopologieAdapter(trainDataClient), new BookingTrainAdapter(bookingReferenceClient));
        }

        public String MakeReservation(ReservationRequestDto request)
        {
            var reservationRequest = request.ToReservationRequest();

            var reservation = _hexagon.Execute(reservationRequest);
            
            return SerializeReservationResponse(ReservationResponseDto.FromReservation(reservationRequest,reservation));
        }

        private static string SerializeReservationResponse(ReservationResponseDto reservation)
        {
            return "{" +
                   "\"train_id\": \"" + reservation.TrainId + "\", " +
                   "\"booking_reference\": \"" + reservation.BookingId + "\", " +
                   "\"seats\": [" + String.Join(", ",
                       reservation.Seats.Select(s => "\"" + s.SeatNumber + s.Coach + "\"")) +
                   "]" +
                   "}";
        }
    }
}
