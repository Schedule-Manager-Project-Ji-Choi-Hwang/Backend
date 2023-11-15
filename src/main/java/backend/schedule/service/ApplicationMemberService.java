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

    /**
     * (신청 멤버 저장)
     * 신청 멤버 저장
     */
    public void save(Member member, StudyPost studyPost) {
        ApplicationMember applicationMember = new ApplicationMember(member, studyPost);
        studyPost.addApplicationMember(applicationMember);
        applicationMemberRepository.save(applicationMember);
    }

    /**
     * (스터디 멤버 저장)
     * 신청 멤버 조회
     */
    public Optional<ApplicationMember> findById(Long id) {
        return applicationMemberRepository.findById(id);
    }

    /**
     * (스터디 멤버 저장)
     * 신청 멤버 삭제
     */
    public void delete(ApplicationMember applicationMember) {
        applicationMemberRepository.delete(applicationMember);
    }

}
