using System.Collections.Generic;
using System.Linq;
using Newtonsoft.Json;
using TrainKata.Domain;
using TrainKata.Infra;

namespace TrainKata
{
    public class GetSncfTopology : GetTopology
    {
        private readonly ITrainDataClient _trainDataClient;

        public GetSncfTopology(ITrainDataClient trainDataClient)
        {
            _trainDataClient = trainDataClient;
        }

        public Topology For(TrainId trainId)
        {
            string sncfTopologyJson = _trainDataClient.GetTopology(trainId.Value);

            return SetupTrain(sncfTopologyJson);
        }

        private static Topology SetupTrain(string sncfTopologyJson)
        {
            var coaches =
                Deserialize(sncfTopologyJson).GroupBy(o => o.coach).Select(values =>
                        new Coach(values.Select(s => new Seat(new SeatId(s.coach, s.seat_number), string.IsNullOrEmpty(s.booking_reference))).ToList()))
                    .ToList();

            return new Topology(coaches);
        }

        private static Dictionary<string, TopologieDto.TopologieSeatDto>.ValueCollection Deserialize(string sncfTopologyJson)
        {
            return JsonConvert.DeserializeObject<TopologieDto>(sncfTopologyJson).seats.Values;
        }
    }
}