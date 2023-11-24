package backend.schedule.service;

import backend.schedule.dto.PersonalSubjectReqDto;
import backend.schedule.dto.PersonalSubjectResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.PersonalSubject;
import backend.schedule.repository.PersonalSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalSubjectService {

    private final PersonalSubjectRepository personalSubjectRepository;


    /**
     * (개인 과목 저장)
     * 개인 과목 저장
     */
    public PersonalSubjectResDto save(PersonalSubjectReqDto personalSubjectReqDto, Member member) {
        // 개인 과목 객체 생성 및 연관 관계 설정
        PersonalSubject personalSubject = new PersonalSubject(personalSubjectReqDto.getSubjectName());
        member.addPersonalSubject(personalSubject);
        
        // 개인 과목 객체 저장
        PersonalSubject savedSubject = personalSubjectRepository.save(personalSubject);
        return new PersonalSubjectResDto(savedSubject);
    }

    /**
     * (개인 과목 단일 조회)
     * 개인 과목 단일 조회
     */
    public PersonalSubject findOne(Long subjectId) {
        // id값 이용해 개인 과목 조회
        Optional<PersonalSubject> optionalPersonalSubject = personalSubjectRepository.findById(subjectId);

        return optionalPersonalSubject.orElse(null);
    }

    /**
     * (개인 과목 전체 조회)
     * 개인 과목 전체 조회 (멤버별)
     */
    public List<PersonalSubjectResDto> findAll(Member member) {
        return personalSubjectRepository.findByMember(member)
                .stream()
                .map(PersonalSubjectResDto::new)
                .collect(Collectors.toList());
    }

    /**
     * (개인 과목 변경)
     * 개인 과목 변경 (제목)
     */
    public void subjectNameUpdate(Long subjectId, PersonalSubjectReqDto personalSubjectReqDto) {
        PersonalSubject findSubject = findOne(subjectId);
        findSubject.subjectNameUpdate(personalSubjectReqDto.getSubjectName());
    }

    /**
     * (개인 과목 삭제)
     * 개인 과목 삭제
     */
    public void subjectDelete(Member member, PersonalSubject personalSubject) {
        member.removeSubject(personalSubject);
        personalSubjectRepository.delete(personalSubject);
    }
}
