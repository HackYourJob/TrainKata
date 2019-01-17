package infra.in;

public class ReservationRequestDto {
	public final String trainId;
    public final int seatCount;

    public ReservationRequestDto(String trainId, int seatCount) {
		this.trainId = trainId;
        this.seatCount = seatCount;
    }

}
