package backend.schedule.service;


import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.ApplicationMemberRepository;
import backend.schedule.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationMemberService {

    private final ApplicationMemberRepository applicationMemberRepository;
    private final StudyMemberRepository studyMemberRepository;

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
    public ApplicationMember findById(Long id) {
        Optional<ApplicationMember> optionalApplicationMember = applicationMemberRepository.findById(id);

        return optionalApplicationMember.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.APPLICATION));
    }

    public boolean ApplicationMemberDuplicate(Member member, StudyPost studyPost) {
        return applicationMemberRepository.existsByMemberAndStudyPost(member, studyPost);
    }

    public boolean StudyMemberDuplicateCheck(Member member, StudyPost studyPost) {
        return studyMemberRepository.existsByMemberAndStudyPost(member, studyPost);
    }

    /**
     * (스터디 멤버 저장)
     * 신청 멤버 삭제
     */
    public void rejectMember(Long apMemberId, StudyPost studyPost, ApplicationMember apMember) {
        studyPost.removeApplicationMember(apMember);
        applicationMemberRepository.deleteByIdAndStudyPost(apMemberId, studyPost);
    }

    public ApplicationMember findApMember(Long applicationMemberId) {
        Optional<ApplicationMember> optionalApplicationMember = applicationMemberRepository.findApMember(applicationMemberId);

        return optionalApplicationMember.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.APPLICATION));
    }

    public void deleteApplicationMember(Long applicationMemberId, ApplicationMember applicationMember, Long studyPostId) {
        applicationMember.getStudyPost().removeApplicationMember(applicationMember); // 편의 메서드 제거
        applicationMemberRepository.deleteApmember(applicationMemberId, applicationMember.getMember().getId(), studyPostId);
    }
}
