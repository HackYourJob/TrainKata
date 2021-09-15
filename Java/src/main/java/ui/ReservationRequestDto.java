package ui;

import domain.ReservationRequest;
import domain.SeatCount;
import domain.TrainId;

public class ReservationRequestDto {
	public final String trainId;
    public final int seatCount;

    public ReservationRequestDto(String trainId, int seatCount) {
		this.trainId = trainId;
        this.seatCount = seatCount;
    }

    public ReservationRequest toDomainModel(){
       return new ReservationRequest(new TrainId(trainId), new SeatCount(seatCount));
    }
}