package domain.ports.in;

import domain.*;
import domain.ports.out.BookTrain;
import domain.ports.out.GetTrainTopology;

import java.util.ArrayList;
import java.util.List;

public class MakeReservation {
    public final GetTrainTopology getTrainTopology;
    public final BookTrain bookTrain;

    public MakeReservation(GetTrainTopology getTrainTopology, BookTrain bookTrain) {
        this.getTrainTopology = getTrainTopology;
        this.bookTrain = bookTrain;
    }

    public ReservationDomainEvent makeReservation(MakeReservationCommand makeReservation) {
        List<Coach> coaches = getTrainTopology.getTrainTopology(makeReservation.trainId);

        Coach foundCoach = tryToFindAvailableCoach(makeReservation, coaches);

        List<Seat> chosenSeats = tryToChooseSeats(makeReservation, foundCoach);

        if (chosenSeats.isEmpty()) {
            return new ReservationFailed(makeReservation.trainId);
        }
        ReservationSucceed reservation = bookTrain.bookTrain(makeReservation.trainId, chosenSeats, foundCoach);
        return reservation;
    }

    public List<Seat> tryToChooseSeats(MakeReservationCommand command, Coach foundCoach) {
        List<Seat> chosenSeats = new ArrayList<Seat>();
        if (foundCoach != null) {
            long limit = command.nbSeats;
            for (Seat seat : foundCoach.seats) {
                if (seat.available) {
                    if (limit-- == 0) break;
                    chosenSeats.add(seat);
                }
            }
        }
        return chosenSeats;
    }

    public Coach tryToFindAvailableCoach(MakeReservationCommand command, List<Coach> coaches) {
        Coach foundCoach = null;


        for (Coach coach : coaches) {
            if (coach.canAcceptMorePeople(command.nbSeats)) {
                foundCoach = coach;
                break;
            }
        }
        return foundCoach;
    }
}