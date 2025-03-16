package csieReserve.service;

import csieReserve.Repository.NoticeRepository;
import csieReserve.domain.Notice;
import csieReserve.dto.request.NoticeRequestDTO;
import csieReserve.dto.response.NoticeResponseDTO;
import csieReserve.exception.ResourceNotFoundException;
import csieReserve.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeMapper noticeMapper;

    @Transactional
    public NoticeResponseDTO.NoticeDTO createNotice(NoticeRequestDTO.NoticeDTO requestDTO){
        Notice notice = noticeMapper.toEntity(requestDTO);
        noticeRepository.save(notice);
        return NoticeMapper.toDTO(notice);
    }
    @Transactional
    public NoticeResponseDTO.NoticeDTO updateNotice(Long id, NoticeRequestDTO.NoticeDTO request){
        Notice existingNotice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 id의 게시물이 존재하지 않습니다."));

        if(existingNotice.getId() == id){
            existingNotice.setContent(request.getContent());
            existingNotice.setTitle(request.getTitle());
            noticeRepository.save(existingNotice);
        }
        return NoticeMapper.toDTO(existingNotice);
    }

    @Transactional
    public void deleteNotice(Long id){
        Notice existingNotice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 id의 게시물이 존재하지 않습니다."));
        noticeRepository.delete(existingNotice);
    }

    @Transactional
    public NoticeResponseDTO.NoticeDTO getNoticeDetails(Long id){
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 id의 게시물이 존재하지 않습니다."));
        return NoticeMapper.toDTO(notice);
    }
    @Transactional
    public NoticeResponseDTO.NoticeListDTO getNoticeList(){
        List<Notice> notices = noticeRepository.findAll();
        return NoticeMapper.toListDTO(notices);
    }
}
