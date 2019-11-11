using System.Linq;
using Newtonsoft.Json;
using TrainKata.Domain;
using TrainKata.Domain.Options;
using TrainKata.Domain.PortOut;

namespace TrainKata.Infra.TrainDataClient
{
    public class TrainDataClientAdaptater : GetTrainTopology
    {
        private readonly ITrainDataClient _trainDataClient;

        public TrainDataClientAdaptater(ITrainDataClient trainDataClient)
        {
            _trainDataClient = trainDataClient;
        }

        public Maybe<Topology> ById(TrainId id)
        {
            var json = _trainDataClient.GetTopology(id.ToString());
            return Maybe.Some(Deserialize(json));
        }

        private static Topology Deserialize(string json)
        {
            return new Topology(
                JsonConvert.DeserializeObject<TopologieDto>(json)
                    .seats.Values
                    .GroupBy(seat => seat.coach)
                    .Select(coach => new Coach(coach.Select(seat => seat.ToSeat()).ToArray()))
                    .ToArray());
        }
    }
}
