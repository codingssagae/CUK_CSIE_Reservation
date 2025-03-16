package csieReserve.controller;

import csieReserve.dto.request.NoticeRequestDTO;
import csieReserve.dto.response.ApiResponse;
import csieReserve.dto.response.NoticeResponseDTO;
import csieReserve.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NoticeController {
    private final NoticeService noticeService;

    @Operation(summary = "공지사항 작성 API",
            description = "for Admin")
    @PostMapping("/admin/notice")
    public ResponseEntity<ApiResponse<NoticeResponseDTO.NoticeDTO>> createNotice(@RequestBody NoticeRequestDTO.NoticeDTO noticeRequestDTO){
            NoticeResponseDTO.NoticeDTO responseDTO = noticeService.createNotice(noticeRequestDTO);
        return ResponseEntity.ok(new ApiResponse<>("게시글 작성이 성공적으로 되었습니다.", responseDTO));
    }

    @Operation(summary = "공지사항 수정 API",
            description = "for Admin")
    @PostMapping("/admin/notice/{id}")
    public ResponseEntity<ApiResponse<NoticeResponseDTO.NoticeDTO>> updateNotice(@PathVariable("id") Long id,
                                                                                 @RequestBody NoticeRequestDTO.NoticeDTO noticeRequestDTO){
        NoticeResponseDTO.NoticeDTO responseDTO = noticeService.updateNotice(id, noticeRequestDTO);
        return ResponseEntity.ok(new ApiResponse<>("게시글 수정이 성공적으로 되었습니다.", responseDTO));
    }

    @Operation(summary = "공지사항 삭제 API",
            description = "for Admin")
    @DeleteMapping("/admin/notice/{id}")
    public ResponseEntity<ApiResponse<NoticeResponseDTO.NoticeDTO>> deleteNotice(@PathVariable("id") Long id){
        noticeService.deleteNotice(id);
        return ResponseEntity.ok(new ApiResponse<>("게시글 삭제가 성공적으로 되었습니다.", null));
    }

    @Operation(summary = "공지사항 상세 조회 API",
            description = "for general, for admin ")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GENERAL')")
    @GetMapping("/notice/{id}")
    public ResponseEntity<ApiResponse<NoticeResponseDTO.NoticeDTO>> getNotice(@PathVariable("id") Long id){
        NoticeResponseDTO.NoticeDTO responseDTO = noticeService.getNoticeDetails(id);
        return ResponseEntity.ok(new ApiResponse<>("게시글 조회가 성공적으로 되었습니다.", responseDTO));
    }

    @Operation(summary = "공지사항 목록 조회 API",
            description = "for general, for admin ")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GENERAL')")
    @GetMapping("/notice")
    public ResponseEntity<ApiResponse<NoticeResponseDTO.NoticeListDTO>> getNoticeList(){
        NoticeResponseDTO.NoticeListDTO responseDTO = noticeService.getNoticeList();
        return ResponseEntity.ok(new ApiResponse<>("게시글 목록 조회가 성공적으로 되었습니다.", responseDTO));
    }
}
