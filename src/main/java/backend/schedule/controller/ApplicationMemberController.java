package backend.schedule.controller;

import backend.schedule.dto.ApplicationMemberDto;
import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ErrorMessage;
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
import java.util.Optional;
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
     * 요청 횟수 : 회
     */
    @PostMapping("/studyboard/{id}/application-member/add")
    public ResponseEntity<?> save(HttpServletRequest request, @PathVariable Long id) {
        // 토큰 추출 및 멤버 식별
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Member member = memberService.getLoginMemberByLoginId(memberLoginId);

        // 스터디 게시글 조회
        StudyPost studyPost = studyPostService.findById(id);
        if (studyPost == null) {
            return ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        }

        // 해당 스터디에 이미 가입되어 있으면 신청이 불가해야한다.
        boolean studyMemberDuplicateCheck = applicationMemberService.StudyMemberDuplicateCheck(member, studyPost);
        boolean applicationMemberDuplicate = applicationMemberService.ApplicationMemberDuplicate(member, studyPost);
        if (applicationMemberDuplicate) {
            return ResponseEntity.badRequest().body("중복 신청은 불가합니다.");
        } else if (studyMemberDuplicateCheck) {
            return ResponseEntity.badRequest().body("이미 가입된 회원입니다.");
        }

            // 신청 멤버 저장
        applicationMemberService.save(member, studyPost);

        // 응답
        return ResponseEntity.ok().build();
    }

    /**
     * 신청 멤버 전체 조회 기능
     * 요청 횟수 : 회
     */
    @GetMapping("/studyboard/{studyboardId}/application-members")
    public ResponseEntity<?> applicationMembers(HttpServletRequest request, @PathVariable Long studyboardId) {
        // 토큰 추출 및 멤버 식별
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();

        // 스터디 멤버 식별 (권한 식별)
        StudyMember studyMember = studyMemberService.findByMemberAndStudyPost(memberId, studyboardId);
        if (studyMember == null) {
            return ResponseEntity.badRequest().body("권한이 없습니다.");
        }

        // 스터디 게시글 조회
        StudyPost studyPost = studyPostService.findById(studyboardId);
        if (studyPost == null) {
            ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        }

        // 반환할 신청 멤버들 Dto 준비
        List<ApplicationMemberDto> ApplicationMemberDtos = studyPost.getApplicationMembers().stream()
                .map(ApplicationMemberDto::new)
                .collect(Collectors.toList());

        // 응답
        return ResponseEntity.ok().body(new Result(ApplicationMemberDtos));
    }

    /**
     * 신청 멤버 삭제 기능
     * 요청 횟수 : 회
     */
    @DeleteMapping("/studyboard/{studyboardId}/application-members/{apMemberId}/delete")
    public ResponseEntity<?> rejectMember(@PathVariable Long studyboardId, @PathVariable Long apMemberId) {
        StudyPost findStudyPost = studyPostService.findById(studyboardId);
        ApplicationMember findApMember = applicationMemberService.findById(apMemberId);

        if (findStudyPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        } else if (findApMember == null){
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(APPLICATION));
        }

        applicationMemberService.rejectMember(apMemberId, findStudyPost, findApMember);

        return ResponseEntity.ok().build();
    }



}
