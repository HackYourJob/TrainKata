namespace TrainKata.Domain.PortOut
{
    public interface ConfirmReservation
    {
        void Execute(Reservation reservation);
    }
}
