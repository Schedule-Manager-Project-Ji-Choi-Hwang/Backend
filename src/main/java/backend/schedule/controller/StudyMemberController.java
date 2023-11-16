package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class StudyMemberController {

    private final StudyMemberService studyMemberService;
    private final ApplicationMemberService applicationMemberService;
    private final StudyPostService studyPostService;

    /**
     * 스터디 멤버 저장 기능
     * 요청 횟수 : 회
     */
    @PostMapping("/studyboard/{studyboardId}/applicationmember/{applicationMemberId}/add")
    public ResponseEntity<?> save(@PathVariable Long studyboardId, @PathVariable Long applicationMemberId) {
        // 신청 멤버 조회
        ApplicationMember applicationMember = applicationMemberService.findById(applicationMemberId);
        if (applicationMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(APPLICATION));
        }

        // 멤버 획득
        Member member = applicationMember.getMember();

        // 스터디 게시글 조회
        StudyPost studyPost = studyPostService.findById(studyboardId);
        if (studyPost == null) {
            ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        }

        // 스터디 멤버 저장
        studyMemberService.save(member, studyPost);

        // 신청 멤버 삭제
        applicationMemberService.rejectMember(applicationMemberId, studyPost, applicationMember);

        // 응답
        return ResponseEntity.ok().body("스터디 멤버에 등록 성공!");
    }

    // 조회

    // 삭제
}
