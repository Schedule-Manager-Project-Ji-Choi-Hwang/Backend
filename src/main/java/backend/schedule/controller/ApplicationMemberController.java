package backend.schedule.controller;

import backend.schedule.dto.ApplicationMemberDto;
import backend.schedule.dto.Result;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ApplicationMemberController {
    private final ApplicationMemberService applicationMemberService;

    private final MemberService memberService;

    private final StudyPostService studyPostService;

    private final StudyMemberService studyMemberService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    @PostMapping("/studyboard/{id}/application-member/add")
    public ResponseEntity<?> save(HttpServletRequest request, @PathVariable Long id) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Member member = memberService.getLoginMemberByLoginId(memberLoginId);

        StudyPost studyPost = studyPostService.findById(id);

        if (studyPost == null) {
            return ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        }

        applicationMemberService.save(member, studyPost);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/studyboard/{studyboardId}/application-members")
    public ResponseEntity<?> applicationMembers(HttpServletRequest request, @PathVariable Long studyboardId) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();
        StudyMember studyMember = studyMemberService.findByMemberAndStudyPost(memberId, studyboardId);
        if (studyMember == null) {
            return ResponseEntity.badRequest().body("권한이 없습니다.");
        }

        StudyPost studyPost = studyPostService.findById(studyboardId);

        if (studyPost == null) {
            ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        }
        List<ApplicationMemberDto> ApplicationMemberDtos = studyPost.getApplicationMembers().stream()
                .map(ApplicationMemberDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(new Result(ApplicationMemberDtos));
    }
}
