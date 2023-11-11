package backend.schedule.service;

import backend.schedule.dto.PersonalSubjectDto;
import backend.schedule.dto.PersonalSubjectResDto;
import backend.schedule.dto.PersonalSubjectsResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.PersonalSubject;
import backend.schedule.repository.PersonalSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalSubjectService {

    private final PersonalSubjectRepository personalSubjectRepository;

    public void save(PersonalSubjectDto personalSubjectDto) {
        PersonalSubject personalSubject = new PersonalSubject(personalSubjectDto.getMember(), personalSubjectDto.getSubjectName());
        personalSubject.getMember().addPersonalSubject(personalSubject);
        personalSubjectRepository.save(personalSubject);
    }

    public PersonalSubject findOne(Long subjectId) {
        Optional<PersonalSubject> optionalPersonalSubject = personalSubjectRepository.findById(subjectId);

        if (optionalPersonalSubject.isEmpty()) {
            return null;
        }

        return optionalPersonalSubject.get();
    }

    public PersonalSubjectsResDto findAll(Member member) {
        List<PersonalSubject> personalSubjects = personalSubjectRepository.findByMember(member);
        PersonalSubjectsResDto personalSubjectsResDto = new PersonalSubjectsResDto();
        for (PersonalSubject personalSubject: personalSubjects) {
            PersonalSubjectResDto dto = new PersonalSubjectResDto();
            dto.setSubjectName(personalSubject.getSubjectName());
            personalSubjectsResDto.getPersonalSubjectResDtoList().add(dto);
        }
        return personalSubjectsResDto;
    }

    public void subjectNameUpdate(Long subjectId, PersonalSubjectDto personalSubjectDto) {
        PersonalSubject findSubject = findOne(subjectId);
        findSubject.subjectNameUpdate(personalSubjectDto.getSubjectName());
    }

    public void subjectDelete(Long subjectId) {
        personalSubjectRepository.deleteById(subjectId);
    }
}
