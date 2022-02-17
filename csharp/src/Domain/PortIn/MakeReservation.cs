using System.Collections.Generic;
using System.Linq;
using TrainKata.Domain.PortOut;

namespace TrainKata.Domain.PortIn;

public class MakeReservation
{
    private readonly GetTopologie _getTopologie;
    private readonly GenerateBookingReference _generateBookingReference;
    private readonly ConfirmReservation _confirmReservation;

    public MakeReservation(GetTopologie getTopologie, GenerateBookingReference generateBookingReference, ConfirmReservation confirmReservation)
    {
        _getTopologie = getTopologie;
        _generateBookingReference = generateBookingReference;
        _confirmReservation = confirmReservation;
    }

    public Reservation? Process(TrainId trainId, int requestedSeatsNb)
    {
        var coaches = GetTopologieOfTrain(trainId);
        var selectedCoach = SelectCoach(requestedSeatsNb, coaches);

        Reservation? reservationOrDefault;
        if (selectedCoach.HasValue)
        {
            var seats = SelectSeats(requestedSeatsNb, selectedCoach.Value);
            reservationOrDefault = BookingSeats(trainId, seats);
        }
        else
        {
            reservationOrDefault = null;
        }

        return reservationOrDefault;
    }

    private List<Coach> GetTopologieOfTrain(TrainId trainId)
    {
        return _getTopologie.OfTrain(trainId);
    }

    private static Coach? SelectCoach(int requestedSeatsNb, List<Coach> coaches)
    {
        foreach (var coach in coaches.Where(coach => coach.FreeSeats.Count >= requestedSeatsNb))
        {
            return coach;
        }
        return null;
    }

    private static List<SeatId> SelectSeats(int requestedSeatsNb, Coach freeSeatsOfACoach)
    {
        return freeSeatsOfACoach.FreeSeats.Take(requestedSeatsNb).ToList();
    }

    private Reservation BookingSeats(TrainId trainId, List<SeatId> seatIds)
    {
        var bookingReference = _generateBookingReference.Execute();
        var reservation = new Reservation(trainId, bookingReference, seatIds);
                
        _confirmReservation.Execute(reservation);
                
        return reservation;
    }

}