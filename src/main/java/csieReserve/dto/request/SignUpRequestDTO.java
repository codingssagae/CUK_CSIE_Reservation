package csieReserve.dto.request;

import lombok.Data;

@Data
public class SignUpRequestDTO {
    private String name;
    private String userStudnetId; // 학번(=아이디)
    private String userPassword; // 비밀번호
}
