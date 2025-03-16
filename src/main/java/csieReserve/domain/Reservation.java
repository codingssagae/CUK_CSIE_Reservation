package csieReserve.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String phoneNumber;

    private ZonedDateTime reservationStartTime;

    private ZonedDateTime reservationEndTime;

    private Integer participantCount;

    private LocalDate reservationDate; // 회의실 이용 예약 날짜

    private LocalDate applicationDate; // 예약을 신청한 날짜

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

}
