package csieReserve.service;

import csieReserve.controller.UserController;
import csieReserve.dto.request.LoginRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.dto.response.UserResponseDTO;
import csieReserve.dto.security.CustomUserDetails;
import csieReserve.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public UserResponseDTO login(LoginRequestDTO requestDTO){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDTO.getStudentId(), requestDTO.getPassword());
//         인증처리
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
//            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
            SecurityContextHolder.getContext().setAuthentication(authentication);
//            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            return userService.getUserDTOBySession();

    }
}
