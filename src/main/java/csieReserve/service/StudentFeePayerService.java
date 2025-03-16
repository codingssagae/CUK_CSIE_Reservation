package csieReserve.service;

import csieReserve.Repository.StudentFeePayerRepository;
import csieReserve.domain.for_admin.FAQ;
import csieReserve.domain.for_admin.StudentFeePayer;
import csieReserve.dto.request.StudentFeePayerRequestDTO;
import csieReserve.dto.response.StudentFeePayerResponseDTO;
import csieReserve.exception.ResourceNotFoundException;
import csieReserve.exception.StudentFeePayerNotFoundException;
import csieReserve.mapper.StudentFeePayerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentFeePayerService {

    private final StudentFeePayerRepository studentFeePayerRepository;
    private final StudentFeePayerMapper studentFeePayerMapper;

    /**
     * 학생회비 납부자 전체 조회
     * */
    public List<StudentFeePayerResponseDTO> getAllStudentFeePayer(){
        return studentFeePayerRepository.findAll().stream()
                .map(studentFeePayerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentFeePayerResponseDTO saveStudentFeePayer(StudentFeePayerRequestDTO requestDTO){
        StudentFeePayer studentFeePayer = StudentFeePayer.builder()
                .name(requestDTO.getName())
                .studentId(requestDTO.getStudentId())
                .build();
        studentFeePayerRepository.save(studentFeePayer);
        return studentFeePayerMapper.toDTO(studentFeePayer);
    }

    /**
    * 학생회비를 낸 사람들 목록 -> 수정 버튼 누르기 -> 해당 학생회비 학생의 고유 아이디(PK)가 전달.
    * */
    @Transactional
    public StudentFeePayerResponseDTO updateStudentFeePayer(Long id, StudentFeePayerRequestDTO requestDTO){
        StudentFeePayer studentFeePayer = studentFeePayerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("ID가 "+id +"인 학생회비 납부자를 찾을 수 없습니다."));
        studentFeePayer.setName(requestDTO.getName());
        studentFeePayer.setStudentId(requestDTO.getStudentId());
        return studentFeePayerMapper.toDTO(studentFeePayer);
    }

    /**
     * 학생회비를 낸 사람들 목록 -> 삭제 버튼 누르기 -> 학생 회비 납부자 DB에서 삭제.
     * */
    @Transactional
    public void deleteStudentFeePayer(Long id){
        if (!studentFeePayerRepository.existsById(id)) {
            throw new ResourceNotFoundException("해당 고유 ID를 가진 학생회비 납부자를 찾을 수 없습니다.");
        }
        studentFeePayerRepository.deleteById(id);
    }

    /**
     * 학생회비 납부 인증
     * */
    public boolean verifyStudentFeePayer(StudentFeePayerRequestDTO requestDTO){
        if (!studentFeePayerRepository.existsByStudentId(requestDTO.getStudentId())) {
            throw new StudentFeePayerNotFoundException("학생회비 납부자 학번이 DB에 존재하지 않습니다: " + requestDTO.getStudentId());
        }

        if (!studentFeePayerRepository.existsByName(requestDTO.getName())) {
            throw new StudentFeePayerNotFoundException("학생회비 납부자 이름이 DB에 존재하지 않습니다: " + requestDTO.getName());
        }
        Optional<StudentFeePayer> studentFeePayerByStudentId = studentFeePayerRepository.findStudentFeePayerByStudentId(requestDTO.getStudentId());
        if(requestDTO.getName().equals(studentFeePayerByStudentId.get().getName())){
            return true;
        }
        else return false;
    }

}
