package csieReserve.dto.request;

import lombok.Data;

@Data
public class ReservationCheckRequestDTO {
    private String reservationDate; // YYYY-MM-DD
    private String reservationStartTime; // HH:MM
    private String reservationEndTime;
}
