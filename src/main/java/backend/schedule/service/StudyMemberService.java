package backend.schedule.service;


import backend.schedule.entity.StudyMember;
import backend.schedule.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;

    public void save(StudyMember studyMember) {
        studyMemberRepository.save(studyMember);
    }

    public Optional<StudyMember> findById(Long id) {
        return studyMemberRepository.findById(id);
    }

    public void delete(StudyMember studyMember) {
        studyMemberRepository.delete(studyMember);
    }

}
