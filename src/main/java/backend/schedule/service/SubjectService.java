package backend.schedule.service;

import backend.schedule.dto.subject.SubjectReqDto;
import backend.schedule.dto.subject.SubjectResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.Subject;
import backend.schedule.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ErrorMessage.*;

@Service
@Transactional
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;


    /**
     * (개인 과목 저장)
     * 개인 과목 저장
     */
    public SubjectResDto save(SubjectReqDto subjectReqDto, Member member) {
        // 개인 과목 객체 생성 및 연관 관계 설정
        Subject subject = new Subject(subjectReqDto.getSubjectName());
        member.addPersonalSubject(subject);
        
        // 개인 과목 객체 저장
        Subject savedSubject = subjectRepository.save(subject);
        return new SubjectResDto(savedSubject);
    }

    /**
     * (개인 과목 단일 조회)
     * 개인 과목 단일 조회
     */
    public Subject findOne(Long subjectId) {
        // id값 이용해 개인 과목 조회
        Optional<Subject> optionalPersonalSubject = subjectRepository.findById(subjectId);

        return optionalPersonalSubject.orElseThrow(() -> new IllegalArgumentException(SUBJECT));
    }

    /**
     * (개인 과목 전체 조회)
     * 개인 과목 전체 조회 (멤버별)
     */
    public List<SubjectResDto> findAll(Member member) {
        return subjectRepository.findByMember(member)
                .stream()
                .map(SubjectResDto::new)
                .collect(Collectors.toList());
    }

    /**
     * (개인 과목 변경)
     * 개인 과목 변경 (제목)
     */
    public void subjectNameUpdate(Long subjectId, SubjectReqDto subjectReqDto) {
        Subject findSubject = findOne(subjectId);
        findSubject.subjectNameUpdate(subjectReqDto.getSubjectName());
    }

    /**
     * (개인 과목 삭제)
     * 개인 과목 삭제
     */
    public void deleteSubject(Subject subject) {
        subjectRepository.delete(subject);
    }
}
