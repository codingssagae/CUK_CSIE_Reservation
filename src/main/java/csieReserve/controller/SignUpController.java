package csieReserve.controller;


import csieReserve.Repository.UserRepository;
import csieReserve.dto.request.SignUpRequestDTO;
import csieReserve.dto.request.StudentFeePayerRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.exception.InvalidPasswordException;
import csieReserve.exception.StudentFeePayerNotFoundException;
import csieReserve.exception.UserAlreadyExistsException;
import csieReserve.service.SignUpService;
import csieReserve.service.StudentFeePayerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignUpController {
    private final StudentFeePayerService studentFeePayerService;
    private final SignUpService signUpService;


    @Operation(summary = "회원가입 전 학생회비 납부 확인 api",
            description = "학생이 회원가입하기 위한 납부 인증이후 리다이렉션 ")
    @PostMapping("/signup/verify")
    public ResponseEntity<ApiResponse<?>> signUpVerify(@RequestBody StudentFeePayerRequestDTO requestDTO){
        String studentId = requestDTO.getStudentId();
        try{
            if(studentFeePayerService.verifyStudentFeePayer(requestDTO)){
                if(signUpService.alreadyUser(studentId)) return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("이미 등록된 학생 ID입니다.",false));
                return ResponseEntity.ok(new ApiResponse<>("납부자 확인 완료, 로그인페이지 리다이렉션",true));
            }
            else return ResponseEntity.ok(new ApiResponse<>("미납부자 확인 완료",false));
        } catch (StudentFeePayerNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(),false));
        }
    }

    @Operation(summary = "회원가입 api",
            description = "학번, 이름, 비밀번호값을 통한 회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signUp(@RequestBody SignUpRequestDTO requestDTO){
        try{
            signUpService.signUp(requestDTO);
            return ResponseEntity.ok(new ApiResponse<>("회원가입 완료",true));
        }catch(UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(e.getMessage(),false));
        }catch(InvalidPasswordException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage(),false));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(e.getMessage(),false));
        }
    }
}
