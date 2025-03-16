package csieReserve.dto.response;

import lombok.Data;

@Data
public class TemporaryPasswordRequestDTO {
    public String studentId;
    public String phoneNumber;
}
