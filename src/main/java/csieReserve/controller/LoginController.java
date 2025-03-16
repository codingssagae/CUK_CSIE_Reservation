package csieReserve.controller;

import csieReserve.dto.request.LoginRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.dto.response.UserResponseDTO;
import csieReserve.dto.response.TemporaryPasswordRequestDTO;
import csieReserve.dto.security.CustomUserDetails;
import csieReserve.jwt.JWTReissueService;
import csieReserve.service.LoginService;
import csieReserve.service.UserService;
import csieReserve.util.CoolSmsUtil;
import csieReserve.util.RandomPasswordGenerator;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {
    private final UserService userService;
    private final LoginService loginService;
    private final CoolSmsUtil coolSmsUtil;
    private final JWTReissueService jwtReissueService;


    @Operation(summary = "로그인 API",
            description = "학번과 비밀번호 전송 후 메인페이지")
    @PostMapping("/login123")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequestDTO requestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        UserResponseDTO responseDTO = UserResponseDTO.builder()
                .userRole(userDetails.getUserRole())  // 예: "ROLE_USER" 또는 "ROLE_ADMIN"
                .studentId(userDetails.getStudentId())
                .name(userDetails.getUsername())
                .build();

        return ResponseEntity.ok(new ApiResponse<>("로그인 성공", responseDTO));
    }


//    @Operation(summary = "로그아웃 API",
//            description = "sessionDB에서 확인 후 로그아웃 진행")
//    @GetMapping("/logout")
//    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            // anonymous유저도 토큰을 가지기 때문에 token의 형태가 무엇인지로 판별해야됨 -> rest api
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            if (!(auth instanceof AnonymousAuthenticationToken)) {
//                new SecurityContextLogoutHandler().logout(request, response, auth);
//            } else if (auth instanceof AnonymousAuthenticationToken) {
//                return ResponseEntity.badRequest().body(new ApiResponse<>("로그인 상태가 아닙니다.", null));
//            }
//            return ResponseEntity.ok(new ApiResponse<>("성공적으로 로그아웃 하였습니다.", null));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(new ApiResponse<>("접근 실패", null));
//        }
//    }

    @Operation(summary = "로그아웃 API",
            description = "sessionDB에서 확인 후 로그아웃 진행")
    @GetMapping("/logout")
    public void logout(HttpServletRequest request) {}

    @Operation(summary = "비밀번호 재설정 API",
            description = "로그인 된 상태에서 비밀번호 재설정")
    @PostMapping("/password/modify")
    public ResponseEntity<ApiResponse<?>> modifyPassword(){
        return ResponseEntity.ok(new ApiResponse<>("sadf",null));
    }

    @Operation(summary = "임시 비밀번호 재발급 API",
            description = "로그인이 안 된 상태에서 임시 비밀번호 발급 후 재설정")
    @PostMapping("/password/temporary")
    public ResponseEntity<ApiResponse<?>> sendTemporaryPassword(@RequestBody TemporaryPasswordRequestDTO temporaryPasswordRequestDTO){
        String message = "[가톨릭대학교 컴퓨터정보공학부 회의실] \n임시 비밀번호 발급되었습니다. \n임시 비밀번호 : " + RandomPasswordGenerator.generatePassword(10);

        /** 임시 비밀번호 자동 설정 서비스 **/

        coolSmsUtil.sendSMS(temporaryPasswordRequestDTO.getPhoneNumber(), message);
        return ResponseEntity.ok(new ApiResponse<>("sadf",null));
    }

    @PreAuthorize("hasAnyRole('ROLE_GENERAL','ROLE_ADMIN')")
    @GetMapping("/main")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // CustomUserDetails는 사용자 정보를 담고 있는 클래스
        jwtReissueService.cleanupExpiredTokens();
        UserResponseDTO dto = userService.getUserDTO(userDetails);
        return ResponseEntity.ok(new ApiResponse<>("사용자 정보 조회 성공", dto));
    }
}


