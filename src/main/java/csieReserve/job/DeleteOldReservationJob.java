package csieReserve.job;

import csieReserve.Repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteOldReservationJob implements Job {
    private final ReservationRepository reservationRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Long reservationId = context.getJobDetail().getJobDataMap().getLong("reservationId");
        reservationRepository.delete(reservationRepository.findById(reservationId).get());
    }
}
