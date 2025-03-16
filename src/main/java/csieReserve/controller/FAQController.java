package csieReserve.controller;

import csieReserve.dto.request.FAQRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.dto.response.FAQResponseDTO;
import csieReserve.service.FAQService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FAQController {

    private final FAQService faqService;

    @Operation(summary = "질의응답(FAQ) 조회 API",
            description = "DB에 저장된 모든 질의응답 조회 API")
    @GetMapping("/faq/getAll")
    public ResponseEntity<ApiResponse<List<FAQResponseDTO>>> getAllFAQs(){
        List<FAQResponseDTO> allFAQs = faqService.getAllFAQs();
        return ResponseEntity.ok(new ApiResponse<>("FAQ 조회 성공",allFAQs));
    }

    @Operation(summary = "질의응답(FAQ) 생성 API",
            description = "질문과 답변을 파라미터로 전송 후 DB에 저장")
    @PostMapping("/admin/faq/create")
    public ResponseEntity<ApiResponse<FAQResponseDTO>> createFAQ(@RequestBody FAQRequestDTO faqRequestDTO){
        FAQResponseDTO faq = faqService.createFAQ(faqRequestDTO);
        return ResponseEntity.ok(new ApiResponse<>("FAQ 성공적으로 저장 되었습니다.", faq));
    }

    @Operation(summary = "질의응답(FAQ) 수정 API",
            description = "질문과 답변을 파라미터로 전송 후 DB에 저장, 만약 답변을 수정해야 할 경우 기존의 제목을 다시 입력해야함")
    @PutMapping("/admin/faq/update/{id}")
    public ResponseEntity<ApiResponse<FAQResponseDTO>> updateFAQ(@PathVariable Long id,
                                    @RequestBody FAQRequestDTO updateFAQ){
        FAQResponseDTO faqResponseDTO = faqService.updateFAQ(id, updateFAQ);
        return ResponseEntity.ok(new ApiResponse<>("성공적으로 업데이트 했습니다",faqResponseDTO));
    }

    @Operation(summary = "질의응답(FAQ) 삭제 API",
            description = "질의응답의 고유 id를 앤드포인트에 넣은 후 해당 질의응답을 DB에서 삭제")
    @DeleteMapping("/admin/faq/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFAQ(@PathVariable Long id) {
        faqService.deleteFAQ(id);
        return ResponseEntity.ok(new ApiResponse<>("아이디가 "+id+"인 FAQ를 성공적으로 삭제했습니다.", null));
    }

}
