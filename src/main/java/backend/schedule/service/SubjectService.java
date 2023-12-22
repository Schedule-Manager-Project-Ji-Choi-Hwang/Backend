package backend.schedule.service;

import backend.schedule.dto.subject.SubjectReqDto;
import backend.schedule.dto.subject.SubjectResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.Subject;
import backend.schedule.repository.MemberRepository;
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
    private final MemberRepository memberRepository;


    /**
     * (개인 과목 저장)
     * 개인 과목 저장
     */
    public void save(SubjectReqDto subjectReqDto, Member member) {
        // 개인 과목 객체 생성 및 연관 관계 설정
        Subject subject = new Subject(subjectReqDto);
        member.addPersonalSubject(subject);
        
        // 개인 과목 객체 저장
        subjectRepository.save(subject);
    }

    /**
     * (개인 과목 단일 조회)
     * 개인 과목 단일 조회
     */
    public Subject findSubjectById(Long subjectId, Long memberId) {
        // id값 이용해 개인 과목 조회
        Optional<Subject> optionalPersonalSubject = subjectRepository.findSubject(subjectId, memberId);

        return optionalPersonalSubject.orElseThrow(() -> new IllegalArgumentException(AUTHORITY));
    }

    /**
     * (개인 과목 전체 조회)
     * 개인 과목 전체 조회 (멤버별)
     */
    public List<SubjectResDto> findSubjects(Long memberId) {
         return subjectRepository.findByMember(memberId)
                .stream()
                .map(SubjectResDto::new)
                .collect(Collectors.toList());
    }

    /**
     * (개인 과목 변경)
     * 개인 과목 변경 (제목)
     */
    public void updateSubjectName(Subject subject, SubjectReqDto subjectReqDto) {
        subject.subjectNameUpdate(subjectReqDto);
    }

    /**
     * (개인 과목 삭제)
     * 개인 과목 삭제
     */
    public void deleteSubject(Subject subject) {
        subjectRepository.delete(subject);
    }
}
