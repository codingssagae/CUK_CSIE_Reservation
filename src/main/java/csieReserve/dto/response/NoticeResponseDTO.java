package csieReserve.dto.response;

import csieReserve.domain.Notice;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class NoticeResponseDTO {

    @Getter
    @Builder
    public static class NoticeDTO{
        private final Long id;
        private final String title;
        private final String content;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;
    }
    @Getter
    @Builder
    public static class NoticeListDTO{
        private List<NoticeDTO> noticeList;
    }

}
