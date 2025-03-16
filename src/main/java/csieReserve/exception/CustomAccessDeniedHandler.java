package csieReserve.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import csieReserve.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // AccessDeniedException을 처리한 후 ResponseEntity를 반환
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        ApiResponse<?> apiResponse = new ApiResponse<>("접근 권한이 없습니다.", null);
        ResponseEntity<ApiResponse<?>> responseEntity = ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
                .body(apiResponse);

        // 응답을 클라이언트로 반환
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403 상태 코드 설정
        response.getWriter().write(objectMapper.writeValueAsString(responseEntity.getBody()));
        response.getWriter().flush();
    }
}
