package csieReserve.controller;

import csieReserve.Repository.UserRepository;
import csieReserve.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserController {
    private final UserRepository userRepository;

    @Operation(summary = "더미 유저 데이터 용",
            description = "더미 유저 데이터 생성")
    @PostMapping("/user/add") // 더미 유저 데이터 용
    public void addUser(){
        User user = new User();
        userRepository.save(user);
    }

}
