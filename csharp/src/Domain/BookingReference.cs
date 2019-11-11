namespace TrainKata.Domain
{
    public struct BookingReference
    {
        public string Reference { get; }

        public BookingReference(string reference)
        {
            Reference = reference;
        }

        public override string ToString()
        {
            return Reference;
        }
    }
}