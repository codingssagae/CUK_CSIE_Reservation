package csieReserve.job;

import csieReserve.Repository.ReservationRepository;
import csieReserve.domain.Reservation;
import csieReserve.domain.ReservationStatus;
import csieReserve.util.CoolSmsUtil;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Optional;

@RequiredArgsConstructor
public class SendReservationReminderJob implements Job {

    private final CoolSmsUtil coolSmsUtil;
    private final ReservationRepository reservationRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long reservationId = context.getJobDetail().getJobDataMap().getLong("reservationId");
        Optional<Reservation> repositoryById = reservationRepository.findById(reservationId);
        Reservation reservation = repositoryById.get();
        if (reservation != null && reservation.getReservationStatus() == ReservationStatus.RESERVED) {
            String message = "[가톨릭대학교 컴퓨터정보공학부 회의실]\n회의실 예약 시간 10분 전입니다.";
            coolSmsUtil.sendSMS(reservation.getPhoneNumber(),message);
        }
    }
}
