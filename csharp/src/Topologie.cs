using System.Collections.Generic;

namespace KataTrainReservation
{
    
        public struct Topologie
        {
            public Topologie(IList<Coach> coaches)
            {
                Coaches = coaches;
            }

            public IEnumerable<Coach> Coaches { get; }
        }
    }