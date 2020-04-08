using System;
using System.Linq;
using TrainKata.Infra;

namespace TrainKata
{
    public class LegacyJsonSerializer
    {
        public static string Serialize(ReservationResponseDto dto)
        {
            return "{" +
                   "\"train_id\": \"" + dto.TrainId + "\", " +
                   "\"booking_reference\": \"" + dto.BookingId + "\", " +
                   "\"seats\": [" + String.Join(", ",
                       dto.Seats.Select(id => "\"" + id + "\"")) +
                   "]" +
                   "}";
        }
    }
}