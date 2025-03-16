package csieReserve.mapper;

import csieReserve.domain.for_admin.FAQ;
import csieReserve.dto.request.FAQRequestDTO;
import csieReserve.dto.response.FAQResponseDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FAQMapper {

    public FAQ toEntity(FAQRequestDTO dto){
        FAQ faq = new FAQ();
        faq.setAnswer(dto.getAnswer());
        faq.setQuestion(dto.getQuestion());
        faq.setCreateAt(LocalDateTime.now());
        return faq;
    }

    public FAQResponseDTO toDTO(FAQ faq){
        FAQResponseDTO dto = new FAQResponseDTO();
        dto.setId(faq.getId());
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        dto.setCreateAt(faq.getCreateAt());
        return dto;
    }

}
