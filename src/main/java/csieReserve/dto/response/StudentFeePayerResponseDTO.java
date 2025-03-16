package csieReserve.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentFeePayerResponseDTO {
    private Long id;
    private String name;
    private String studentId;
}
