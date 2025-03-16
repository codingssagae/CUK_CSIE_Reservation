package csieReserve.Repository;

import csieReserve.domain.for_admin.StudentFeePayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFeePayerRepository extends JpaRepository<StudentFeePayer, Long> {
    Optional<StudentFeePayer> findStudentFeePayerByName(String name);
    Optional<StudentFeePayer> findStudentFeePayerByStudentId(String name);
    boolean existsByStudentId(String studentId);
    boolean existsByName(String name);
}
