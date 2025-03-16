package csieReserve.service;

import csieReserve.Repository.FAQRepository;
import csieReserve.domain.for_admin.FAQ;
import csieReserve.dto.request.FAQRequestDTO;
import csieReserve.dto.response.FAQResponseDTO;
import csieReserve.exception.ResourceNotFoundException;
import csieReserve.mapper.FAQMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;
    private final FAQMapper faqMapper;

    // FAQ 전체 조회
    public List<FAQResponseDTO> getAllFAQs(){
        return faqRepository.findAll().stream()
                .map(faqMapper::toDTO)
                .collect(Collectors.toList());
    }

    // FAQ 생성
    @Transactional
    public FAQResponseDTO createFAQ(FAQRequestDTO requestDTO){
        FAQ faq = faqMapper.toEntity(requestDTO);
        faqRepository.save(faq);
        return faqMapper.toDTO(faq);
    }

    // FAQ 수정
    @Transactional
    public FAQResponseDTO updateFAQ(Long id, FAQRequestDTO updatedFAQ){
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("ID가 "+id +"인 FAQ를 찾을 수 없습니다."));
        faq.setQuestion(updatedFAQ.getQuestion());
        faq.setAnswer(updatedFAQ.getAnswer());
        faqRepository.save(faq);
        return faqMapper.toDTO(faq);
    }

    // FAQ 삭제
    @Transactional
    public void deleteFAQ(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new ResourceNotFoundException("FAQ with ID: " + id + " 찾을 수 없습니다.");
        }
        faqRepository.deleteById(id);
    }


}
