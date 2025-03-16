package csieReserve.mapper;

import csieReserve.domain.for_admin.StudentFeePayer;
import csieReserve.dto.request.StudentFeePayerRequestDTO;
import csieReserve.dto.response.StudentFeePayerResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class StudentFeePayerMapper {

    public StudentFeePayer toEntity(StudentFeePayerRequestDTO dto){
        return StudentFeePayer.builder()
                .studentId(dto.getStudentId())
                .name(dto.getName())
                .build();
    }

    public StudentFeePayerResponseDTO toDTO(StudentFeePayer studentFeePayer){
        return StudentFeePayerResponseDTO
                .builder()
                .id(studentFeePayer.getId())
                .name(studentFeePayer.getName())
                .studentId(studentFeePayer.getStudentId())
                .build();
    }

}
