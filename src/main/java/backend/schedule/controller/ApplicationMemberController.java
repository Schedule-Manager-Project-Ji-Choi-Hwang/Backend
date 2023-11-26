package backend.schedule.controller;

import backend.schedule.dto.applicationmember.ApplicationMemberDto;
import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
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
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class ApplicationMemberController {

    private final ApplicationMemberService applicationMemberService;
    private final MemberService memberService;
    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
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
    @PostMapping("/studyboard/{studyboardId}/application-member/add")
    public ResponseEntity<?> save(HttpServletRequest request, @PathVariable Long studyboardId) {
        //스터디 포스트 addApplicationMember 편의 메서드에 들어가는거도 없음
        try {
            String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
            String secretKey = mySecretkey;
            String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
            Member member = memberService.getLoginMemberByLoginId(memberLoginId);

            StudyPost studyPost = studyPostService.findById(studyboardId);

            // 해당 스터디에 이미 가입되어 있으면 신청이 불가해야한다.
            boolean studyMemberDuplicateCheck = applicationMemberService.StudyMemberDuplicateCheck(member, studyPost);
            boolean applicationMemberDuplicate = applicationMemberService.ApplicationMemberDuplicate(member, studyPost);
            if (applicationMemberDuplicate) {
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(DUPLICATE));
            } else if (studyMemberDuplicateCheck) {
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(ALREADY));
            }

            applicationMemberService.save(member, studyPost);

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
    @GetMapping("/studyboard/{studyboardId}/application-members")
    public ResponseEntity<?> applicationMembers(HttpServletRequest request, @PathVariable Long studyboardId) {
        //재검토
        try {
            String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
            String secretKey = mySecretkey;
            String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
            Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);

            // 스터디 멤버 식별 (권한 식별)
            studyMemberService.findByMemberAndStudyPost(findMember.getId(), studyboardId);

            // 스터디 게시글 조회
            StudyPost studyPost = studyPostService.findById(studyboardId);

            // 반환할 신청 멤버들 Dto 준비 (페치조인으로 바꿔야함 멤버가 3명이면 멤버 닉네임을 가져오기 위해 3번 쿼리가 더 나가고 100명이면 100번 더 나감)
            List<ApplicationMemberDto> ApplicationMemberDtos = studyPost.getApplicationMembers().stream()
                    .map(ApplicationMemberDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(new Result(ApplicationMemberDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
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
    @DeleteMapping("/studyboard/{studyboardId}/application-members/{apMemberId}/delete")
    public ResponseEntity<?> rejectMember(@PathVariable Long studyboardId, @PathVariable Long apMemberId) {
        //studyPostService, applicationMemberService에서 각각 findById하는게 아니라 applicationMemberService에서
        //신청테이블, 회원, 게시글 id(pk) 맞는지 확인 후 삭제해야 할듯 -> applicationMemberService에서 다 처리
        //리더가 아니여도 삭제 가능, studyboardId가 있기만하면 아무거나 넣어도 삭제가능, StudyPost 편의 메서드에서 제거기능 추가
        try {
            StudyPost findStudyPost = studyPostService.findById(studyboardId);
            ApplicationMember findApMember = applicationMemberService.findById(apMemberId);

            applicationMemberService.rejectMember(apMemberId, findStudyPost, findApMember);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}
