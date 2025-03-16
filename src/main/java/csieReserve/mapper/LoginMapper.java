package csieReserve.mapper;

import csieReserve.dto.response.UserResponseDTO;

public class LoginMapper {
    public static UserResponseDTO toDTO(String role, String studentId, String name){
        return UserResponseDTO.builder()
                .userRole(role)
                .studentId(studentId)
                .name(name)
                .build();
    }
}
