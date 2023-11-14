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

    public void save(PersonalSubjectDto personalSubjectDto, Member member) {
        PersonalSubject personalSubject = new PersonalSubject(personalSubjectDto.getSubjectName());
        member.addPersonalSubject(personalSubject);
        personalSubjectRepository.save(personalSubject);
    }

    public PersonalSubject findOne(Long subjectId) {
        Optional<PersonalSubject> optionalPersonalSubject = personalSubjectRepository.findById(subjectId);

        return optionalPersonalSubject.orElse(null);
    }

    public List<PersonalSubjectResDto> findAll(Member member) {
        return personalSubjectRepository.findByMember(member)
                .stream()
                .map(PersonalSubjectResDto::new)
                .collect(Collectors.toList());
    }

    public void subjectNameUpdate(Long subjectId, PersonalSubjectDto personalSubjectDto) {
        PersonalSubject findSubject = findOne(subjectId);
        findSubject.subjectNameUpdate(personalSubjectDto.getSubjectName());
    }

    public void subjectDelete(Long subjectId) {
        personalSubjectRepository.deleteById(subjectId);
    }
}
