package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.StudyMemberResDto;
import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class StudyMemberController {

    private final StudyMemberService studyMemberService;
    private final ApplicationMemberService applicationMemberService;
    private final StudyPostService studyPostService;

    /**
     * 스터디 멤버 저장 기능
     * 요청 횟수 : 6회
     *          1. 신청 멤버 조회
     *          2. 스터디 게시글 조회
     *          3. 스터디 멤버 등록
     *          4. 스터디 게시글 id로 신청 멤버 조회 (??)
     *          5. 신청 멤버 id와 스터디 게시글 id로 신청 멤버 조회
     *          6. 신청 멤버 삭제
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
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        }

        // 스터디 멤버 저장
        studyMemberService.save(member, studyPost);

        // 신청 멤버 삭제
        applicationMemberService.rejectMember(applicationMemberId, studyPost, applicationMember);

        // 응답
        return ResponseEntity.ok().body("스터디 멤버에 등록 성공!");
    }

    /**
     * 스터디 멤버 전체 조회 기능
     * 요청 횟수 : 1 + N회
     *          1. 스터디 게시글 조회
     *          2. 멤버 수 만큼 조회 (닉네임 출력때문에 member로 타고 들어가기 때문)
     */
    @GetMapping("/studyboard/{studyboardId}/studyMembers")
    public ResponseEntity<?> findStudyMembers(@PathVariable Long studyboardId) {
        List<StudyMemberResDto> studyMembers = studyMemberService.findStudyMembers(studyboardId);
        if (studyMembers == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        }

        return ResponseEntity.ok().body(new Result(studyMembers));
    }

    /**
     * 스터디 멤버 삭제 기능
     * 요청 횟수 : 4회
     *          1. 스터디 게시글 조회
     *          2. 스터디 멤버 조회
     *          3. 스터디 게시글 id로 스터디 멤버 조회 (? 이게 왜 발생?)
     *          4. 스터디 멤버 삭제
     */
    @DeleteMapping("/studyboard/{studyboardId}/studyMembers/{studyMemberId}")
    public ResponseEntity<?> deleteStudyMember(@PathVariable Long studyboardId, @PathVariable Long studyMemberId) {
        StudyPost studyPost = studyPostService.findById(studyboardId);
        StudyMember studyMember = studyMemberService.findById(studyMemberId);

        if (studyPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        } else if (studyMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(STUDY));
        }

        studyMemberService.delete(studyPost, studyMember);

        return ResponseEntity.ok().build();
    }
}
