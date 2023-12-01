package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.studymember.StudyMemberResDto;
import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.MemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static backend.schedule.enumlist.ConfirmAuthor.*;
import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class StudyMemberController {

    private final StudyMemberService studyMemberService;
    private final JwtTokenExtraction jwtTokenExtraction;
    private final ApplicationMemberService applicationMemberService;
    private final StudyPostService studyPostService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스터디 멤버 저장 기능
     * 요청 횟수 : 6회
     * 1. 신청 멤버 조회
     * 2. 스터디 게시글 조회
     * 3. 스터디 멤버 등록
     * 4. 스터디 게시글 id로 신청 멤버 조회 (??)
     * 5. 신청 멤버 id와 스터디 게시글 id로 신청 멤버 조회
     * 6. 신청 멤버 삭제
     */
    @PostMapping("/studyboard/{studyBoardId}/applicationmember/{applicationMemberId}/add")
    public ResponseEntity<?> save(@PathVariable Long studyBoardId, @PathVariable Long applicationMemberId, HttpServletRequest request) {
        //여기도 리더권한 확인하는거 없음 -> 리더만 수락 가능하게
        //studyBoardId DB에 저장된 번호 아무거나 써도 작동문제
        try {
            Member member = jwtTokenExtraction.extractionMember(request, mySecretkey);
            studyMemberService.studyMemberSearch(member.getId(), studyBoardId, LEADER);

//            ApplicationMember applicationMember = applicationMemberService.findById(applicationMemberId); // 신청 멤버 조회 편의메서드 삭제때문에 필요
            StudyPost studyPost = studyPostService.findById(studyBoardId); // 스터디 게시글 조회 편의메서드 때문

            studyMemberService.save(member, studyPost); // 스터디 멤버 저장
            applicationMemberService.rejectMember(applicationMemberId, studyPost); // 신청 멤버 삭제


            return ResponseEntity.ok().body("스터디 멤버에 등록 성공!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 멤버 전체 조회 기능
     * 요청 횟수 : 1 + N회
     * 1. 스터디 게시글 조회
     * 2. 멤버 수 만큼 조회 (닉네임 출력때문에 member로 타고 들어가기 때문)
     */
    @GetMapping("/studyboard/{studyBoardId}/studyMembers")
    public ResponseEntity<?> findStudyMembers(@PathVariable Long studyBoardId, HttpServletRequest request) {
        //스터디 멤버 맞는지 확인 로직 추가

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);
            List<StudyMemberResDto> studyMembers = studyMemberService.findStudyMembers(studyBoardId);


            return ResponseEntity.ok().body(new Result(studyMembers));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스터디 멤버 탈퇴 기능(아무나 가능)
     * 요청 횟수 : 4회
     * 1. 스터디 게시글 조회
     * 2. 스터디 멤버 조회
     * 3. 스터디 게시글 id로 스터디 멤버 조회 (? 이게 왜 발생?)
     * 4. 스터디 멤버 삭제
     */
    @DeleteMapping("/studyboard/{studyBoardId}/studyMemberwithdrawal")
    public ResponseEntity<?> deleteStudyMember(HttpServletRequest request, @PathVariable Long studyBoardId) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            StudyMember studyMember = studyMemberService.studyMemberGetStudyPost(memberId, studyBoardId);
            studyMemberService.deleteStudyMember(studyMember);

//            StudyPost studyPost = studyPostService.findById(studyBoardId);
//            StudyMember studyMember = studyMemberService.findById(studyMemberId);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}
