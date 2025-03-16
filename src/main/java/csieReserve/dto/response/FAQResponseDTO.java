package csieReserve.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FAQResponseDTO {
    private Long id;
    private String question;
    private String answer;
    private LocalDateTime createAt;
}
