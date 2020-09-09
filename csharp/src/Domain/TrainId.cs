namespace KataTrainReservation
{
    public struct TrainId
    {
        public TrainId(string id)
        {
            Id = id;
        }

        public string Id { get; }
    }
}