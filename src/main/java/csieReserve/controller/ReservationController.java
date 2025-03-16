package csieReserve.controller;

import csieReserve.Repository.UserRepository;
import csieReserve.domain.BlockDate;
import csieReserve.dto.request.ReservationByDateRequestDTO;
import csieReserve.dto.request.ReservationCheckRequestDTO;
import csieReserve.dto.request.ReservationRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.dto.response.ReservationResponseDTO;
import csieReserve.dto.security.CustomUserDetails;
import csieReserve.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "해당 유저의 전체 회의실 예약 내역 조회 API",
            description = "DB에 저장된 해당 유저의 전체 회의실 예약 내역 조회 API")
    @GetMapping("/reservation/get")
    public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getUserReservations(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(new ApiResponse<>("해당 유저의 모든 예약 내역 조회 완료",
                reservationService.getUserReservations(
                        ((CustomUserDetails)principal).getId())));
    }

    @Operation(summary = "회의실 예약 생성 API",
            description = "회의실 예약 생성 및 확인 문자 자동 발송 API")
    @PostMapping("/reservation/create")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> createReservation(@RequestBody ReservationRequestDTO requestDTO,
                                                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) throws SchedulerException {
        return ResponseEntity.ok(new ApiResponse<>("성공적으로 예약 저장 완료 및 예약 확인 문자 발송",
                reservationService.createReservation(requestDTO, customUserDetails.getId())));
    }

    @Operation(summary = "회의실 예약 취소 API",
            description = "회의실 예약 취소 및 예약 상태 변경 API")
    @PutMapping("/reservation/cancel/{id}")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> deleteReservation(@PathVariable Long id) throws SchedulerException {
        return ResponseEntity.ok(new ApiResponse<>("성공적으로 예약을 취소 하였습니다",
                reservationService.cancelReservation(id)));
    }

    @Operation(summary = "해당 년-월 모든 예약 조회 API",
            description = "년-월을 받아 해당하는 서비스의 모든 예약 조회 API")
    @PostMapping("/reservation/get/byDate")
    public ResponseEntity<ApiResponse<List<ReservationResponseDTO>>> getReservationsByDate(@RequestBody ReservationByDateRequestDTO requestDTO){
        return ResponseEntity.ok(new ApiResponse<>("성공적으로 해당 년-월 예약 조회 완료",
                reservationService.getAllReservationsByYearAndMonth(requestDTO)));
    }

    @Operation(summary = "예약 가능 한지 확인 API",
            description = "날짜와 시작 시간, 종료 시간을 받아 해당 날짜와 중복되는 예약이 있는지 조회 및 확인")
    @PostMapping("/reservation/check")
    public ResponseEntity<ApiResponse<Boolean>> checkReservationAvailable(@RequestBody ReservationCheckRequestDTO requestDTO) {
        boolean result = reservationService.isReservationAvailable(requestDTO);
        if (result) return ResponseEntity.ok(new ApiResponse<>("해당 날짜에 해당하는 시간 예약 가능합니다.", result));
        else return ResponseEntity.ok(new ApiResponse<>("해당 날짜에 해당하는 시간에는 이미 예약이 있습니다.", result));
    }

    @Operation(summary = "해당 달의 예약 마감 된 날짜 반환 API",
            description = "년-월을 받아 해당 달에 예약 마감 된 예약 날짜를 반환 API")
    @GetMapping("/reservation/check-fully-reserved")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getFullyReservedDate(@RequestParam int year, @RequestParam int month) {
        List<LocalDate> fullyReserveDates = reservationService.getFullyReserveDates(year, month);
        return ResponseEntity.ok(new ApiResponse<>(year + "년 " + month + "월의 예약 마감된 날짜 조회 완료", fullyReserveDates));
    }

    @Operation(summary = "해당 달의 예약 차단 생성 API",
            description = "년-월-일을 받아 예약 차단, 기존 예약이 있는 경우 삭제하고 취소 문자를 보내는 API")
    @PostMapping("/admin/reservation/block")
    public ResponseEntity<ApiResponse<List<Void>>> blockReservationDay(@RequestBody ReservationRequestDTO.BlockDateDTO blockDateDTO) {
        reservationService.blockReservationDay(blockDateDTO);
        reservationService.deleteReservationForce(blockDateDTO);
        return ResponseEntity.ok(new ApiResponse<>(blockDateDTO.getDate() + "예약 차단", null));
    }


    @Operation(summary = "해당 달의 예약 차단 삭제 API",
            description = "년-월-일을 받아 예약 차단한것 삭제 하는 API")
    @PostMapping("/admin/reservation/block/delete")
    public ResponseEntity<ApiResponse<List<Void>>> deleteBlockReservationDay(@RequestBody ReservationRequestDTO.BlockDateDTO blockDateDTO) {
        reservationService.deleteBlockReservationDay(blockDateDTO);
        return ResponseEntity.ok(new ApiResponse<>(blockDateDTO.getDate() + "예약 차단 삭제", null));
    }

    @Operation(summary = "해당 달의 예약 차단 조회API",
            description = "년-월-일을 받아 예약 차단된 일 조회 하는 API")
    @GetMapping("/admin/reservation/block")
    public ResponseEntity<ApiResponse<List<ReservationRequestDTO.BlockDateDTO>>> blockReservationDay(@RequestParam int year, @RequestParam int month) {
        List<ReservationRequestDTO.BlockDateDTO> list = reservationService.getBlockReservationDay(year, month);
        return ResponseEntity.ok(new ApiResponse<>(year + "년 " + month + "월", list));
    }

}
