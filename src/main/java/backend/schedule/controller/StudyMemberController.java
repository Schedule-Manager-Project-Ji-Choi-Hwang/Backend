package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.studymember.StudyMemberResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
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

import static backend.schedule.enumlist.ConfirmAuthor.LEADER;

@RestController
@RequiredArgsConstructor
public class StudyMemberController {

    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
    private final JwtTokenExtraction jwtTokenExtraction;
    private final ApplicationMemberService applicationMemberService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스터디 멤버 저장 기능
     */
    @PostMapping("/study-board/{studyBoardId}/application-member/{applicationMemberId}/study-member/add")
    public ResponseEntity<?> save(@PathVariable Long studyBoardId, @PathVariable Long applicationMemberId, HttpServletRequest request) {
        try {
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);
            studyMemberService.studyMemberSearch(findMember.getId(), studyBoardId, LEADER);

            Member joinMember = applicationMemberService.findApplicationMember(applicationMemberId, studyBoardId).getMember();

            StudyPost studyPost = studyPostService.findById(studyBoardId);

            studyMemberService.save(joinMember, studyPost);
            applicationMemberService.rejectMember(applicationMemberId, studyBoardId);

            return ResponseEntity.ok().body("스터디 멤버에 등록 성공!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스터디 멤버 전체 조회 기능
     */
    @GetMapping("/study-board/{studyBoardId}/study-members")
    public ResponseEntity<?> findStudyMembers(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);

            StudyPost findStudyPost = studyPostService.returnToStudyMembers(studyBoardId);

            List<StudyMemberResDto> studyMembers = studyMemberService.findStudyMembers(findStudyPost);

            return ResponseEntity.ok().body(new Result(studyMembers));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스터디 멤버 탈퇴 기능(아무나 가능)
     */
    @DeleteMapping("/study-board/{studyBoardId}/study-member/withdrawal")
    public ResponseEntity<?> deleteStudyMember(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            StudyMember studyMember = studyMemberService.studyMemberGetStudyPost(memberId, studyBoardId);
            studyMemberService.deleteStudyMember(studyMember);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스터디 멤버 강퇴 기능(리더만 이용 가능)
     */
    @DeleteMapping("/study-board/{studyBoardId}/study-member/{studyMemberId}/expulsion")
    public ResponseEntity<?> expulsionStudyMember(@PathVariable Long studyBoardId, @PathVariable Long studyMemberId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);

            String expulsionStudyMember = studyMemberService.expulsionStudyMember(studyBoardId, studyMemberId);

            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(expulsionStudyMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }
}
