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
     * 요청 횟수 : 5회
     * 1. 멤버 조회
     * 2. 스터디 게시글 조회
     * 3. 스터디 멤버인지 체크
     * 4. 중복 신청인지 체크
     * 5. 신청 멤버 저장
     */
    @PostMapping("/study-board/{studyBoardId}/application-member/add")
    public ResponseEntity<?> save(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);

            StudyPost studyPost = studyPostService.findById(studyBoardId);

            // 해당 스터디에 이미 가입되어 있으면 신청이 불가해야한다.
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
     * 요청 횟수 : 5회
     * 1. 멤버 조회
     * 2. 스터디 멤버 권한 조회
     * 3. 스터디 게시글 조회
     * 4. 신청 멤버들 조회
     * 5. 신청 멤버 닉네임 조회 (Dto 생성 부분인듯)
     */
    @GetMapping("/study-board/{studyBoardId}/application-members")
    public ResponseEntity<?> applicationMembers(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);

            // 스터디 멤버 식별 (권한 식별)
            studyMemberService.studyMemberSearch(findMember.getId(), studyBoardId, ConfirmAuthor.LEADER);

            // 스터디 게시글 조회 (민현 추가 - join fetch 버전)
            StudyPost studyPost = studyPostService.findStudyPostByApplicationMembers(studyBoardId);

            // 반환할 신청 멤버들 Dto 준비 (페치조인으로 바꿔야함 멤버가 3명이면 멤버 닉네임을 가져오기 위해 3번 쿼리가 더 나가고 100명이면 100번 더 나감)
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
     * 요청 횟수 : 5회
     * 1. 게시글 조회
     * 2. 신청 멤버 조회
     * 3. 스터디 게시글 id로 신청 멤버 조회
     * 4. 스터디 게시글 id로 신청 멤버 조회 (deleteBy로 인해 한번 더 조회하는 듯)
     * 5. 신청 멤버 삭제
     */
    @DeleteMapping("/study-board/{studyBoardId}/application-members/{apMemberId}/delete")
    public ResponseEntity<?> rejectMember(@PathVariable Long studyBoardId, @PathVariable Long apMemberId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            // 권한 식별
            studyMemberService.studyMemberSearch(memberId, studyBoardId, ConfirmAuthor.LEADER);

            String deleteApplicationMember = applicationMemberService.deleteApplicationMember(apMemberId, studyBoardId);

            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(deleteApplicationMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}
