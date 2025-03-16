package csieReserve.Repository;

import csieReserve.domain.BlockDate;
import csieReserve.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BlockDateRepository extends JpaRepository<BlockDate, Long> {


    @Query("SELECT u FROM BlockDate u WHERE YEAR(u.date) = :year AND MONTH(u.date) = :month")
    List<BlockDate> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT u FROM BlockDate u WHERE u.date = :date")
    Optional<BlockDate> findByDate(@Param("date") LocalDate date);
}
