package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.applicationmember.ApplicationMemberDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplicationMemberController {

    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
    private final JwtTokenExtraction jwtTokenExtraction;
    private final ApplicationMemberService applicationMemberService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 신청 멤버 저장 기능
     */
    @PostMapping("/study-board/{studyBoardId}/application-member/add")
    public ResponseEntity<?> save(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);
            StudyPost studyPost = studyPostService.findById(studyBoardId);

            applicationMemberService.StudyMemberDuplicateCheck(findMember.getId(), studyBoardId);
            applicationMemberService.ApplicationMemberDuplicate(findMember.getId(), studyBoardId);

            applicationMemberService.save(findMember, studyPost);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 신청 멤버 전체 조회 기능
     */
    @GetMapping("/study-board/{studyBoardId}/application-members")
    public ResponseEntity<?> applicationMembers(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);
            studyMemberService.studyMemberSearch(findMember.getId(), studyBoardId, ConfirmAuthor.LEADER);

            StudyPost studyPost = studyPostService.findStudyPostByApplicationMembers(studyBoardId);

            List<ApplicationMemberDto> applicationMemberDtos = applicationMemberService.applicationMemberList(studyPost);

            return ResponseEntity.ok().body(new Result(applicationMemberDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        } catch (ArrayIndexOutOfBoundsException e) {
            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(e.getMessage()));
        }

    }

    /**
     * 신청 멤버 삭제 기능
     */
    @DeleteMapping("/study-board/{studyBoardId}/application-members/{apMemberId}/delete")
    public ResponseEntity<?> rejectMember(@PathVariable Long studyBoardId, @PathVariable Long apMemberId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, ConfirmAuthor.LEADER);

            String deleteApplicationMember = applicationMemberService.deleteApplicationMember(apMemberId, studyBoardId);

            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(deleteApplicationMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}
