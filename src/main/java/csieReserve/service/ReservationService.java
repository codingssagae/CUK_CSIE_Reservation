package csieReserve.service;

import csieReserve.Repository.BlockDateRepository;
import csieReserve.Repository.ReservationRepository;
import csieReserve.Repository.UserRepository;
import csieReserve.domain.BlockDate;
import csieReserve.domain.Reservation;
import csieReserve.dto.request.ReservationByDateRequestDTO;
import csieReserve.dto.request.ReservationCheckRequestDTO;
import csieReserve.dto.request.ReservationRequestDTO;
import csieReserve.dto.response.ReservationResponseDTO;
import csieReserve.exception.ResourceNotFoundException;
import csieReserve.job.DeleteOldReservationJob;
import csieReserve.job.SendReservationReminderJob;
import csieReserve.job.UpdateReservationStatusJob;
import csieReserve.mapper.ReservationMapper;
import csieReserve.util.CoolSmsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static csieReserve.domain.ReservationStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BlockDateRepository blockDateRepository;
    private final Scheduler scheduler;
    private final CoolSmsUtil smsUtil;
    private final ReservationMapper reservationMapper;
    private final UserRepository userRepository;

    /**
     * 예약 생성
     * -> 예약 생성 시 자동으로 예약자 번호로 확인 문자 전송
     * -> 예약 10분 전 확인 문자 Quartz 스케쥴링을 통해 예약 전송
     * */
    @Transactional
    public ReservationResponseDTO createReservation(ReservationRequestDTO requestDTO, Long userId) throws SchedulerException{
        Reservation reservation = reservationMapper.toEntity(requestDTO, userId);
        reservation.setReservationStatus(RESERVED);

        reservationRepository.save(reservation); // 예약 저장

        // 예약 성공 문자 발송하기
        try {
            String successMessage = "[가톨릭대학교 컴퓨터정보공학부 회의실]\n예약이 성공적으로 완료되었습니다.";
            smsUtil.sendSMS(reservation.getPhoneNumber(), successMessage);
        }
        catch (Exception e){
            e.printStackTrace();
            System.err.println("e = " + e);
        }
        // 예약 10분 전 확인 문자 보내기
        ZonedDateTime reminderZonedDateTime = reservation.getReservationStartTime()
                .minusMinutes(10)
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"));

        Date reminderTime = Date.from(reminderZonedDateTime.toInstant());
        System.out.println("reminderTime = " + reminderTime);

        if (reminderTime.after(new Date())){
            JobDetail reminderJob = JobBuilder.newJob(SendReservationReminderJob.class)
                    .withIdentity("reminderJob_"+reservation.getId(),"reservationJobs")
                    .usingJobData("reservationId", reservation.getId())
                    .build();

            Trigger reminderTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("reminderTrigger_"+reservation.getId(),"reservationTriggers")
                    .startAt(reminderTime)
                    .build();

            scheduler.scheduleJob(reminderJob,reminderTrigger);
        }

        /**
         * 예약 시간이 되어 사용 중 일때 예약 객체의 상태 변경
         */
        ZonedDateTime statusChangeZonedDateTime = reservation.getReservationStartTime()
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"));

        Date statusChangeTime = Date.from(statusChangeZonedDateTime.toInstant());

        log.info("Current time: {}", new Date());
        log.info("Status change time: {}", statusChangeTime);

        if (statusChangeTime.after(new Date())) {
            JobDetail statusChangeJob = JobBuilder.newJob(UpdateReservationStatusJob.class)
                    .withIdentity("statusChangeJob_"+reservation.getId(),"reservationJobs")
                    .usingJobData("reservationId", reservation.getId())
                    .build();

            Trigger statusChangeTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("statusChangeTrigger_"+reservation.getId(),"reservationTriggers")
                    .startAt(statusChangeTime)
                    .build();

            scheduler.scheduleJob(statusChangeJob,statusChangeTrigger);
        }

        /**
         * 예약 이 후 30일이 지나면 예약 내역(DB)에서 자동 삭제
         * */

        ZonedDateTime deleteJobZonedDateTime = reservation.getApplicationDate()
                .atStartOfDay(ZoneId.of("Asia/Seoul"))
                .plusDays(30);

        Date deleteJobTime = Date.from(deleteJobZonedDateTime.toInstant());

        JobDetail deleteReservationJob = JobBuilder.newJob(DeleteOldReservationJob.class)
                .withIdentity("deleteReservationJob_"+ reservation.getId(), "reservationJobs")
                .usingJobData("reservationId", reservation.getId())
                .build();

        Trigger deleteReservationTrigger = TriggerBuilder.newTrigger()
                .withIdentity("deleteReservationTrigger_"+ reservation.getId(), "reservationTriggers")
                .startAt(deleteJobTime)
                .build();

        scheduler.scheduleJob(deleteReservationJob,deleteReservationTrigger);


        return reservationMapper.toDTO(reservation);
    }

    /**
     * 예약 취소
     * */
    @Transactional
    public ReservationResponseDTO cancelReservation(Long reservationId) throws SchedulerException{
        Optional<Reservation> repositoryById = reservationRepository.findById(reservationId);
        Reservation reservation = repositoryById.get();
        if(reservation !=null && reservation.getReservationStatus()==RESERVED){
            reservation.setReservationStatus(CANCELLED);
            reservationRepository.save(reservation);

            // Quartz 스케쥴링 된 Job 제거
            scheduler.deleteJob(JobKey.jobKey("reminderJob_"+reservationId,"reminderJobs"));
            scheduler.deleteJob(JobKey.jobKey("reminderTrigger_" + reservationId, "reminderTriggers"));
            scheduler.deleteJob(JobKey.jobKey("statusChangeJob_"+reservationId,"reservationJobs"));
            scheduler.deleteJob(JobKey.jobKey("statusChangeTrigger"+reservationId,"reservationTriggers"));
        }
        return reservationMapper.toDTO(reservation);
    }

    /**
     * 예약자 고유 id를 가져와 모든 예약을 조회
     * */
    public List<ReservationResponseDTO> getUserReservations(Long userId){
        userRepository.findById(userId)
                .orElseThrow(()->new ResourceNotFoundException("해당 유저를 못찾았습니다")); // 404

        return reservationRepository.findByUserId(userId).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 년-월 별 서비스의 전체 예약 내역 조회
     * */
    public List<ReservationResponseDTO> getAllReservationsByYearAndMonth(ReservationByDateRequestDTO requestDTO){
        return reservationRepository.findAllByYearAndMonth(requestDTO.getYear(), requestDTO.getMonth(),RESERVED,USING)
                .stream().map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 해당 날짜와 시간에 예약이 이미 존재하는지 확인
     * */
    @Transactional(readOnly = true)
    public boolean isReservationAvailable(ReservationCheckRequestDTO requestDTO){
        LocalDate reservationDate = LocalDate.parse(requestDTO.getReservationDate());
        LocalTime startTime = LocalTime.parse(requestDTO.getReservationStartTime());
        LocalTime endTime = LocalTime.parse(requestDTO.getReservationEndTime());

        ZonedDateTime reservationStartTime = ZonedDateTime.of(reservationDate, startTime, ZoneId.of("Asia/Seoul"));
        ZonedDateTime reservationEndTime = ZonedDateTime.of(reservationDate, endTime, ZoneId.of("Asia/Seoul"));

        List<Reservation> exactMatchingReservations = reservationRepository.findExactMatchingReservations(reservationDate, reservationStartTime, reservationEndTime, RESERVED, USING);
        return exactMatchingReservations.isEmpty();
    }

    /**
     * 특정 년-월에서 예약이 꽉 찬 날짜를 반환
     */
    @Transactional(readOnly = true)
    public List<LocalDate> getFullyReserveDates(int year, int month) {
        List<Reservation> reservations = reservationRepository.findAllByYearAndMonth(year, month, RESERVED, USING);
        Map<LocalDate, List<Reservation>> reservationByDate = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getReservationDate));

        List<LocalDate> fullyReservedDates = new ArrayList<>();
        LocalTime[] timeSlots = {
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                LocalTime.of(18, 0),
                LocalTime.of(20, 0)
        };

        for (Map.Entry<LocalDate, List<Reservation>> entry : reservationByDate.entrySet()) {
            LocalDate reservationDate = entry.getKey();
            List<Reservation> daliyReservations = entry.getValue();

            boolean isFullyReserved = Arrays.stream(timeSlots)
                    .allMatch(time -> daliyReservations.stream()
                            .anyMatch(reservation -> reservation.getReservationStartTime().toLocalTime().equals(time)));

            if (isFullyReserved) fullyReservedDates.add(reservationDate);
        }

        return fullyReservedDates;
    }


    public void blockReservationDay(ReservationRequestDTO.BlockDateDTO blockDateDTO) {
        if (blockDateDTO == null || blockDateDTO.getDate() == null) {
            throw new IllegalArgumentException("차단할 날짜가 필요합니다.");
        }

        if (blockDateRepository.findByDate(blockDateDTO.getDate()).isPresent()) {
            throw new IllegalArgumentException("이미 예약이 차단된 날짜입니다.");
        }

        BlockDate date = ReservationMapper.toBlockDateEntity(blockDateDTO.getDate());
        blockDateRepository.save(date);
    }

    public void deleteBlockReservationDay(ReservationRequestDTO.BlockDateDTO blockDateDTO) {
        blockDateRepository.findByDate(blockDateDTO.getDate())
                .ifPresentOrElse(blockDateRepository::delete,
                        () -> { throw new IllegalArgumentException("삭제 할 수 없습니다. 예약 차단이 되어 있지 않습니다."); });
    }

    public List<ReservationRequestDTO.BlockDateDTO> getBlockReservationDay(int year, int month) {
        List<BlockDate> dates = Optional.ofNullable(blockDateRepository.findByYearAndMonth(year, month))
                .orElse(Collections.emptyList());

        return dates.stream()
                .map(ReservationMapper::toBlockDateDTO)
                .collect(Collectors.toList());
    }

    /**
     * 예약 날짜 차단 할 때 이미 존재하는 예약을 강제 제거
     */
    @Transactional
    public void deleteReservationForce(ReservationRequestDTO.BlockDateDTO blockDateDTO) {
        int year = blockDateDTO.getDate().getYear();
        int month = blockDateDTO.getDate().getMonthValue();
        int day = blockDateDTO.getDate().getDayOfMonth();

        List<Reservation> reservations = reservationRepository.findAllByYearAndMonthAndDay(year, month, day, RESERVED, USING);
        if (reservations.isEmpty()) return;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Reservation reservation : reservations) {
            reservationRepository.delete(reservation); // 우선 DB에서 예약 삭제

            String startTimeStr = reservation.getReservationStartTime().toLocalTime().format(timeFormatter);
            String endTimeStr = reservation.getReservationEndTime().toLocalTime().format(timeFormatter);

            String reservationInfo = month + "월 " + day + "일 " + startTimeStr + "~" + endTimeStr;

            String smsContent = "안녕하세요, 학우님.\n\n" +
                    reservationInfo + " 예약하신 컴퓨터정보공학부 회의실이 부득이한 사정으로 임시 폐쇄되어 예약이 취소되었습니다.\n" +
                    "자세한 사유는 공지사항을 참고 부탁드립니다.\n\n" +
                    "불편을 드려 죄송합니다.";

            smsUtil.sendLMS(reservation.getPhoneNumber(), smsContent);
        }
    }

}
