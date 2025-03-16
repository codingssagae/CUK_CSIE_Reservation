package csieReserve.Repository;

import csieReserve.domain.Reservation;
import csieReserve.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import retrofit2.http.PartMap;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long id);

    @Query("SELECT r FROM Reservation r WHERE FUNCTION('YEAR', r.reservationDate) = :year AND FUNCTION('MONTH', r.reservationDate) = :month")
    List<Reservation> findAllByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT r FROM Reservation r WHERE FUNCTION('YEAR', r.reservationDate) = :year AND FUNCTION('MONTH', r.reservationDate) = :month AND "+
            "r.reservationStatus IN (:RESERVED, :USING)")
    List<Reservation> findAllByYearAndMonth(@Param("year") int year, @Param("month") int month,
                                            @Param("RESERVED") ReservationStatus reserved, @Param("USING") ReservationStatus using);

    @Query("SELECT r FROM Reservation r WHERE r.reservationDate = :date AND"+
            "(r.reservationStartTime = :startTime AND r.reservationEndTime = :endTime) AND " +
            "r.reservationStatus IN (:RESERVED, :USING)")
    List<Reservation> findExactMatchingReservations(@Param("date")LocalDate date,
                                                    @Param("startTime")ZonedDateTime startTime,
                                                    @Param("endTime") ZonedDateTime endTime,
                                                    @Param("RESERVED")ReservationStatus reserved,
                                                    @Param("USING") ReservationStatus using);

    @Query("SELECT r FROM Reservation r WHERE FUNCTION('YEAR', r.reservationDate) =:year AND FUNCTION('MONTH', r.reservationDate) =:month " +
            "AND FUNCTION('DAY', r.reservationDate) =:day AND r.reservationStatus IN (:RESERVED, :USING)")
    List<Reservation> findAllByYearAndMonthAndDay(@Param("year") int year, @Param("month") int month, @Param("day") int day,
                                                  @Param("RESERVED") ReservationStatus reserved, @Param("USING") ReservationStatus using);

}
