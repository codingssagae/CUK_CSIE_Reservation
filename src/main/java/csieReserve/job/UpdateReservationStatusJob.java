package csieReserve.job;

import csieReserve.Repository.ReservationRepository;
import csieReserve.domain.Reservation;
import csieReserve.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static csieReserve.domain.ReservationStatus.*;

@Component
@RequiredArgsConstructor
public class UpdateReservationStatusJob implements Job {
    private final ReservationRepository reservationRepository;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Long reservationId = jobExecutionContext.getJobDetail().getJobDataMap().getLong("reservationId");
        Optional<Reservation> repositoryById = reservationRepository.findById(reservationId);
        Reservation reservation = repositoryById.get();

        if (reservation.getReservationStatus()== RESERVED){
            reservation.setReservationStatus(USING);
            reservationRepository.save(reservation);
        }
    }
}
