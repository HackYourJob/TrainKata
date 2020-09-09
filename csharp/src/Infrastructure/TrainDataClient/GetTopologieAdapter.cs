using System;
using System.Collections.Generic;
using System.Linq;
using Newtonsoft.Json;

namespace KataTrainReservation
{
    public class GetTopologieAdapter : GetTopologie
    {
        private readonly ITrainDataClient _client;

        public GetTopologieAdapter(ITrainDataClient client)
        {
            _client = client;
        }

        public Topologie GetTopologie(TrainId id)
        {
            string data = _client.GetTopology(id.Id);

            var topologieDto = DeserializeInToTopologie(data);

            return new Topologie(topologieDto.Values.Select(coachDto =>
            {
                return new Coach(
                    coachDto.Select(seatDto=> seatDto.ToSeat())
                        .ToList());
            }).ToList());
        }

        private static Dictionary<string, List<TopologieDto.TopologieSeatDto>> DeserializeInToTopologie(string data)
        {
            Dictionary<String, List<TopologieDto.TopologieSeatDto>> coachesByCoachId =
                new Dictionary<string, List<TopologieDto.TopologieSeatDto>>();
            foreach (var seat in JsonConvert.DeserializeObject<TopologieDto>(data).seats.Values)
            {
                if (!coachesByCoachId.ContainsKey(seat.coach))
                    coachesByCoachId.Add(seat.coach, new List<TopologieDto.TopologieSeatDto>());
                coachesByCoachId[seat.coach].Add(seat);
            }

            return coachesByCoachId;
        }
    }
}