package csieReserve.dto.response;

import csieReserve.domain.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Data
@Builder
public class ReservationResponseDTO {
    private Long id;
    private Long userId;
    private String phoneNumber;
    private ZonedDateTime reservationStartTime;
    private ZonedDateTime reservationEndTime;
    private Integer participantCount;
    private LocalDate reservationDate;
    private LocalDate applicationDate;
    private ReservationStatus reservationStatus;
}
