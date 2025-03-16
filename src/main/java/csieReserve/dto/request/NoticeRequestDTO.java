package csieReserve.dto.request;

import lombok.Data;

public class NoticeRequestDTO {
    @Data
    public static class NoticeDTO{
        String content;
        String title;
    }
}
