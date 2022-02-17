using System.Collections.Generic;
using System.Linq;
using Newtonsoft.Json;
using TrainKata.Domain;
using TrainKata.Domain.PortOut;

namespace TrainKata.Infra;

public class GetTopologieAdaptater : GetTopologie
{
    private readonly ITrainDataClient _trainDataClient;

    public GetTopologieAdaptater(ITrainDataClient trainDataClient)
    {
        _trainDataClient = trainDataClient;
    }

    public List<Coach> OfTrain(TrainId trainId)
    {
        var seatsByCoachId = GetTopology(trainId.ToString());

        var coaches = seatsByCoachId.Select(kv => new Coach(kv.Value.Where(s => s.booking_reference == "")
            .Select(s => new SeatId(new CoachId(s.coach), new SeatNumber(s.seat_number))).ToList())).ToList();
        return coaches;
    }

    private Dictionary<string, List<Topologie.TopologieSeat>> GetTopology(string trainId)
    {
        var serializedTopology = _trainDataClient.GetTopology(trainId);

        var seatsByCoachId = new Dictionary<string, List<Topologie.TopologieSeat>>();
        foreach (var seat in JsonConvert.DeserializeObject<Topologie>(serializedTopology).seats.Values)
        {
            if (!seatsByCoachId.ContainsKey(seat.coach))
                seatsByCoachId.Add(seat.coach, new List<Topologie.TopologieSeat>());
            seatsByCoachId[seat.coach].Add(seat);
        }

        return seatsByCoachId;
    }
}