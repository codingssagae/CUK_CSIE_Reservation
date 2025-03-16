package csieReserve.mapper;

import csieReserve.domain.Notice;
import csieReserve.dto.request.NoticeRequestDTO;
import csieReserve.dto.response.NoticeResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class NoticeMapper {
    public static Notice toEntity(NoticeRequestDTO.NoticeDTO requestDTO){
        return Notice.builder()
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .build();
    }
    public static NoticeResponseDTO.NoticeDTO toDTO(Notice notice){
        return NoticeResponseDTO.NoticeDTO.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
    public static NoticeResponseDTO.NoticeListDTO toListDTO(List<Notice> notices){
        List<NoticeResponseDTO.NoticeDTO> list = notices.stream().map(NoticeMapper::toDTO).collect(Collectors.toList());
        return NoticeResponseDTO.NoticeListDTO.builder()
                .noticeList(list)
                .build();
    }
}
