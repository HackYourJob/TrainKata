using System.Collections.Generic;

namespace TrainKata
{
    public struct Topology
    {
        public ICollection<Coach> Coaches { get; }

        public Topology(ICollection<Coach> coaches)
        {
            Coaches = coaches;
        }
    }
}