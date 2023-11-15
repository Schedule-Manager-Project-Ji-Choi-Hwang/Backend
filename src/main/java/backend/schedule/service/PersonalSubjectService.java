package backend.schedule.service;

import backend.schedule.dto.PersonalSubjectDto;
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
    public void save(PersonalSubjectDto personalSubjectDto, Member member) {
        PersonalSubject personalSubject = new PersonalSubject(personalSubjectDto.getSubjectName());
        member.addPersonalSubject(personalSubject);
        personalSubjectRepository.save(personalSubject);
    }

    /**
     * (개인 과목 단일 조회)
     * 개인 과목 단일 조회
     */
    public PersonalSubject findOne(Long subjectId) {
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
    public void subjectNameUpdate(Long subjectId, PersonalSubjectDto personalSubjectDto) {
        PersonalSubject findSubject = findOne(subjectId);
        findSubject.subjectNameUpdate(personalSubjectDto.getSubjectName());
    }

    /**
     * (개인 과목 삭제)
     * 개인 과목 삭제
     */
    public void subjectDelete(Long subjectId) {
        personalSubjectRepository.deleteById(subjectId);
    }
}
