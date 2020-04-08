using TrainKata.Domain;
using TrainKata.Infra;

namespace TrainKata
{
    public class TicketOfficeService
    {
        private readonly MakeReservation _makeReservation;

        public TicketOfficeService(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
        {
            _makeReservation = new MakeReservation(new GetSncfTopology(trainDataClient), new BookSncfReservation(bookingReferenceClient));
        }

        public string MakeReservation(ReservationRequestDto request)
        {
            var reservation = _makeReservation.Execute(request.ToDomain());

            return SerializeReservation(reservation, new TrainId(request.TrainId));
        }

        private static string SerializeReservation(Reservation? reservation, TrainId trainId)
        {
            var dto = reservation.HasValue 
                ? ReservationResponseDto.Succeeded(reservation.Value)
                : ReservationResponseDto.Refused(trainId);

            return LegacyJsonSerializer.Serialize(dto);
        }
    }
}
