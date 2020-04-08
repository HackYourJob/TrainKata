using TrainKata.Domain;
using TrainKata.Infra;

namespace TrainKata
{
    public class MakeReservationAdapter
    {
        private readonly MakeReservation _makeReservation;

        public MakeReservationAdapter(ITrainDataClient trainDataClient, IBookingReferenceClient bookingReferenceClient)
            : this(new MakeReservation(new GetSncfTopology(trainDataClient), new BookSncfReservation(bookingReferenceClient)))
        {
        }

        public MakeReservationAdapter(GetTopology getTopology, BookReservation bookReservation)
            : this(new MakeReservation(getTopology, bookReservation))
        {
        }

        public MakeReservationAdapter(MakeReservation makeReservation)
        {
            _makeReservation = makeReservation;
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
