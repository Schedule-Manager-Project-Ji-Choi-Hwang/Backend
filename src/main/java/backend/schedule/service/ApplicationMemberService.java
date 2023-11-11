package backend.schedule.service;


import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyPost;
import backend.schedule.repository.ApplicationMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationMemberService {

    private final ApplicationMemberRepository applicationMemberRepository;

    public void save(Member member, StudyPost studyPost) {
        ApplicationMember applicationMember = new ApplicationMember(member, studyPost);
        studyPost.addApplicationMember(applicationMember);
        applicationMemberRepository.save(applicationMember);
    }

    public Optional<ApplicationMember> findById(Long id) {
        return applicationMemberRepository.findById(id);
    }

    public void delete(ApplicationMember applicationMember) {
        applicationMemberRepository.delete(applicationMember);
    }

}
