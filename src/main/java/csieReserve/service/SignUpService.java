package csieReserve.service;

import csieReserve.Repository.UserRepository;
import csieReserve.domain.User;
import csieReserve.domain.UserRole;
import csieReserve.dto.request.SignUpRequestDTO;
import csieReserve.exception.InvalidPasswordException;
import csieReserve.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static csieReserve.domain.UserRole.ROLE_ADMIN;
import static csieReserve.domain.UserRole.ROLE_GENERAL;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private boolean isValidPassword(String password) {
        String regex = "^[a-z0-9]{5,10}$";  // 정규 표현식
        return Pattern.matches(regex, password);
    }

    public void signUp(SignUpRequestDTO signUpRequestDTO){
        User user = new User();
        String password = signUpRequestDTO.getUserPassword();
        if (alreadyUser(signUpRequestDTO.getUserStudnetId())) {throw new UserAlreadyExistsException("이미 등록된 학생 ID입니다.");}
        if (password == null || !isValidPassword(password)) {throw new InvalidPasswordException("비밀번호는 영소문자와 숫자의 조합으로 5~10자여야 합니다.");}

            // 사용자 정보 설정 및 저장
            user.setName(signUpRequestDTO.getName());
            user.setStudentId(signUpRequestDTO.getUserStudnetId());
            user.setPassword(bCryptPasswordEncoder.encode(password));
            user.setUserRole(ROLE_ADMIN);
            userRepository.save(user);
    }
    public boolean alreadyUser(String studnetId){
        return userRepository.existsByStudentId(studnetId);
    }
}

