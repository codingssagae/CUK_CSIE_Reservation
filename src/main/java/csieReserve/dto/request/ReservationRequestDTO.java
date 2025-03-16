package csieReserve.dto.request;

import csieReserve.domain.ReservationStatus;
import csieReserve.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ReservationRequestDTO {

    @Schema(description = "문자 받을 예약자 전화번호", example = "01012345678")
    @Pattern(regexp = "^010\\d{8}$", message = "유효한 전화번호 형식이 아니다")
    private String phoneNumber;

    @Schema(description = "예약 시작 시간 (HH:mm)", example = "10:00")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "시간 형식은 HH:mm 이어야 한다")
    private String reservationStartTime;

    @Schema(description = "예약 종료 시간 (HH:mm)", example = "12:00")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "시간 형식은 HH:mm 이어야 한다")
    private String reservationEndTime;

    @Schema(description = "회의실 참가 인원 수", example = "5")
    @Min(value = 1, message = "참가자 수는 최소 2명 이상")
    private Integer participantCount;

    @Schema(description = "회의실 사용 예약 날짜 (yyyy-MM-dd)", example = "2025-01-03")
    private LocalDate reservationDate;

    @Schema(description = "회의실 예약을 한 신청 날짜 (yyyy-MM-dd)", example = "2025-01-03")
    private LocalDate applicationDate;

    /**
     *  reservationStatus
     *  --> 이것들은 컨트롤러에서 값 넣어야 함
     * */

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BlockDateDTO{
        private LocalDate date;
    }
}
