package backend.schedule.controller;

import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyPost;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyMemberController {

    private final StudyMemberService studyMemberService;

    private final ApplicationMemberService applicationMemberService;

    private final StudyPostService studyPostService;

    @PostMapping("/studyboard/{studyboardId}/applicationmember/{applicationMemberId}/add")
    public ResponseEntity<?> save(@PathVariable Long studyboardId, @PathVariable Long applicationMemberId) {
        ApplicationMember applicationMember = applicationMemberService.findById(applicationMemberId).get();
        Member member = applicationMember.getMember();

        StudyPost studyPost = studyPostService.findById(studyboardId);

        if (studyPost == null) {
            ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        }

        studyMemberService.save(member, studyPost);

        applicationMemberService.delete(applicationMember);

        return ResponseEntity.ok().body("스터디 멤버에 등록 성공!");
    }
}
