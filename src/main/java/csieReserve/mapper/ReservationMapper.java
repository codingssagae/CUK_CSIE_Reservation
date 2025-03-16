package csieReserve.mapper;

import csieReserve.Repository.UserRepository;
import csieReserve.domain.BlockDate;
import csieReserve.domain.Reservation;
import csieReserve.domain.User;
import csieReserve.dto.request.ReservationRequestDTO;
import csieReserve.dto.response.ReservationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Block;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationMapper {

    private final UserRepository userRepository;
    public Reservation toEntity(ReservationRequestDTO dto, Long userId){
        Optional<User> byId = userRepository.findById(userId);

        // 한국 시간으로 변경
        LocalTime startTime = LocalTime.parse(dto.getReservationStartTime());
        LocalTime endTime = LocalTime.parse(dto.getReservationEndTime());

        ZonedDateTime startZonedDateTime = dto.getReservationDate()
                .atTime(startTime)
                .atZone(ZoneId.of("Asia/Seoul"));

        ZonedDateTime endZonedDateTime = dto.getReservationDate()
                .atTime(endTime)
                .atZone(ZoneId.of("Asia/Seoul"));

        return Reservation.builder()
                .user(byId.get())
                .phoneNumber(dto.getPhoneNumber())
                .reservationStartTime(startZonedDateTime)
                .reservationEndTime(endZonedDateTime)
                .participantCount(dto.getParticipantCount())
                .reservationDate(dto.getReservationDate())
                .applicationDate(dto.getApplicationDate())
                .build();
    }

    public ReservationResponseDTO toDTO(Reservation reservation){
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .userId(reservation.getUser().getId())
                .phoneNumber(reservation.getPhoneNumber())
                .reservationStartTime(reservation.getReservationStartTime())
                .reservationEndTime(reservation.getReservationEndTime())
                .participantCount(reservation.getParticipantCount())
                .reservationDate(reservation.getReservationDate())
                .applicationDate(reservation.getApplicationDate())
                .reservationStatus(reservation.getReservationStatus())
                .build();
    }

    public static BlockDate toBlockDateEntity(LocalDate blockDate){
        return BlockDate.builder()
                .date(blockDate)
                .build();
    }

    public static ReservationRequestDTO.BlockDateDTO toBlockDateDTO(BlockDate blockDate){
        return ReservationRequestDTO.BlockDateDTO.builder()
                .date(blockDate.getDate())
                .build();
    }

}
