namespace KataTrainReservation
{
    public struct BookingReference
    {
        public BookingReference(string reference)
        {
            Reference = reference;
        }

        public string Reference { get; }
    }
}