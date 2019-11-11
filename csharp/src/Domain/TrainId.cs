namespace TrainKata.Domain
{
    public struct TrainId
    {
        public string Id { get; }

        public TrainId(string id)
        {
            Id = id;
        }

        public override string ToString()
        {
            return Id;
        }
    }
}