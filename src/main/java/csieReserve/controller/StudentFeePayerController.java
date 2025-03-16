package csieReserve.controller;

import csieReserve.dto.request.StudentFeePayerRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.dto.response.StudentFeePayerResponseDTO;
import csieReserve.exception.StudentFeePayerNotFoundException;
import csieReserve.service.StudentFeePayerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentFeePayerController {

    private final StudentFeePayerService studentFeePayerService;

    @Operation(summary = "학생회비 납부자 전체 조회 API",
            description = "DB에 저장된 전체 학생회비 납부자 조회 API")
    @GetMapping("/admin/studentFeePayer/getAll")
    public ResponseEntity<ApiResponse<List<StudentFeePayerResponseDTO>>> getAllStudentFeePayer(){
        return ResponseEntity.ok(new ApiResponse<>("전제 학생회비 납부자 조회 성공", studentFeePayerService.getAllStudentFeePayer()));
    }

    @Operation(summary = "학생회비 납부자 저장 API",
            description = "이름과 학번을 파라미터로 전송 후 DB에 저장")
    @PostMapping("/admin/studentFeePayer/create")
    public ResponseEntity<ApiResponse<StudentFeePayerResponseDTO>> createStudentFeePayer(@RequestBody StudentFeePayerRequestDTO requestDTO){
        return ResponseEntity.ok(new ApiResponse<>("학생회비 납부자 성공적으로 저장 완료",
                studentFeePayerService.saveStudentFeePayer(requestDTO)
                ));
    }

    @Operation(summary = "학생회비 납부자 정보 수정 API",
            description = "수정하고자 하는 학생회비 고유 ID과 이름, 학번을 파라미터로 전송 후 DB에 수정")
    @PutMapping("/admin/studentFeePayer/update/{id}")
    public ResponseEntity<ApiResponse<StudentFeePayerResponseDTO>> updateStudentFeePayer(@PathVariable Long id, @RequestBody StudentFeePayerRequestDTO requestDTO){
        return ResponseEntity.ok(new ApiResponse<>(
                "성공적으로 수정 완료",
                studentFeePayerService.updateStudentFeePayer(id,requestDTO)
        ));
    }

    @Operation(summary = "학생회비 납부자 정보 삭제 API",
            description = "삭제하고자 하는 학생회비 납부자의 고유 ID를 전송 후 DB에서 삭제")
    @DeleteMapping("/admin/studentFeePayer/delete/{id}")
    public ResponseEntity<ApiResponse<StudentFeePayerResponseDTO>> deleteStudentFeePayer(@PathVariable Long id){
        studentFeePayerService.deleteStudentFeePayer(id);
        return ResponseEntity.ok(new ApiResponse<>("성공적으로 고유 ID가 "+id+"인 학생회비 납부자 삭제 완료", null));
    }

    @Operation(summary = "학생회비 납부자 검증 API",
            description = "확인하고자 할 이름과 학번을 받아 DB에 저장된 학생회비 납부자인지 확인")
    @PostMapping("/admin/studentFeePayer/verify")
    public ResponseEntity<ApiResponse<?>> verifyStudentFeePayer(@RequestBody StudentFeePayerRequestDTO requestDTO){
        try{
            if(studentFeePayerService.verifyStudentFeePayer(requestDTO)){
                return ResponseEntity.ok(new ApiResponse<>("해당 학생은 학생회비 납부자 입니다",true));
            }
            else return ResponseEntity.ok(new ApiResponse<>("해당 학생은 학생회비 납부자가 아닙니다",false));
        } catch (StudentFeePayerNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(),false));
        }
    }
}
