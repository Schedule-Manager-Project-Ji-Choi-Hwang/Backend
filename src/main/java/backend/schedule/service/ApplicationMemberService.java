package backend.schedule.service;


import backend.schedule.dto.applicationmember.ApplicationMemberDto;
import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.ApplicationMemberRepository;
import backend.schedule.repository.StudyMemberRepository;
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
public class ApplicationMemberService {

    private final ApplicationMemberRepository applicationMemberRepository;
    private final StudyMemberRepository studyMemberRepository;

    /**
     * (신청 멤버 저장)
     * 신청 멤버 저장
     */
    public void save(Member member, StudyPost studyPost) {
        ApplicationMember applicationMember = new ApplicationMember(member);
        studyPost.addApplicationMember(applicationMember);
        applicationMemberRepository.save(applicationMember);
    }

    /**
     * (스터디 멤버 저장)
     * 신청 멤버 조회
     */
    public ApplicationMember findApplicationMember(Long applicationMemberId, Long StudyBoardId) {
        Optional<ApplicationMember> optionalApplicationMember = applicationMemberRepository.findApMember(applicationMemberId, StudyBoardId);

        return optionalApplicationMember.orElseThrow(() -> new IllegalArgumentException(APPLICATION));
    }

    public void ApplicationMemberDuplicate(Long memberId, Long studyPostId) {
        boolean findApMember = applicationMemberRepository.existsApplicationMember(memberId, studyPostId);

        if (findApMember) throw new IllegalArgumentException(DUPLICATE);

    }

    public void StudyMemberDuplicateCheck(Long memberId, Long studyBoardId) {
        boolean findStudyMember = studyMemberRepository.existsStudyMember(memberId, studyBoardId);

        if (findStudyMember) throw new IllegalArgumentException(ALREADY);
    }

    /**
     *
     * 권한 체크용
     */
    public boolean applicationButtonCheck(Long memberId, Long studyPostId) {
        return applicationMemberRepository.existsApplicationMember(memberId, studyPostId);

    }

    public boolean studyMemberButtonCheck(Long memberId, Long studyBoardId) {
        return studyMemberRepository.existsStudyMember(memberId, studyBoardId);

    }

    /**
     * (스터디 멤버 저장)
     * 신청 멤버 삭제
     */
    public void rejectMember(Long apMemberId, Long studyBoardId) {
        applicationMemberRepository.deleteByIdAndStudyPost(apMemberId, studyBoardId);
    }

    public List<ApplicationMemberDto> applicationMemberList(StudyPost studyPost) {
        return studyPost.getApplicationMembers().stream()
                .map(ApplicationMemberDto::new)
                .collect(Collectors.toList());
    }

    public String deleteApplicationMember(Long applicationMemberId, Long studyPostId) {
        int deleteApmember = applicationMemberRepository.deleteApmember(applicationMemberId, studyPostId);

        if (deleteApmember == 1) return DELETE;
        else throw new IllegalArgumentException(NOTDELETE);
    }

    public void ApplicationMembersWithdrawal(Long memberId) {
        applicationMemberRepository.ApplicationMembersWithdrawal(memberId);
    }
}
