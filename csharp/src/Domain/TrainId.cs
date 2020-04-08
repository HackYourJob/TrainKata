namespace TrainKata.Domain
{
    public struct TrainId
    {
        public string Value { get; }

        public TrainId(string value)
        {
            Value = value;
        }
    }
}