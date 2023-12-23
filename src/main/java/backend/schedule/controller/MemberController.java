package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.member.*;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.MemberService;
import backend.schedule.service.RefreshTokenService;
import backend.schedule.service.StudyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

import static backend.schedule.enumlist.ErrorMessage.TOKEN;
import static backend.schedule.validation.RequestDataValidation.beanValidation;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenExtraction jwtTokenExtraction;
    private final StudyMemberService studyMemberService;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationMemberService applicationMemberService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;
    @Value("${spring.jwt.token.access.expire}")
    private String myAccessTokenExpireMs;
    @Value("${spring.jwt.token.refresh.expire}")
    private String myRefreshTokenExpireMs;

    /**
     * 회원 가입 기능
     */
    @PostMapping("/member/sign-up")
    public ResponseEntity<?> join(@Validated @RequestBody MemberJoinReqDto memberJoinReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            memberService.checkLoginIdDuplicate(memberJoinReqDto.getLoginId());
            memberService.checkNicknameDuplicate(memberJoinReqDto.getNickname());
            memberService.checkEmailDuplicate(memberJoinReqDto.getEmail());

            memberService.join(memberJoinReqDto);

            return ResponseEntity.ok().body("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 로그인 기능
     */
    @PostMapping("/member/log-in")
    public ResponseEntity<?> login(@Validated @RequestBody MemberLoginReqDto memberLoginReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            Member member = memberService.login(memberLoginReqDto);

            long accessTokenExpireMs = Long.parseLong(myAccessTokenExpireMs);
            long refreshTokenExpireMs = Long.parseLong(myRefreshTokenExpireMs);

            String accessToken = JwtTokenUtil.createAccessToken(member.getLoginId(), mySecretkey, accessTokenExpireMs);
            String refreshToken = JwtTokenUtil.createRefreshToken(mySecretkey, refreshTokenExpireMs);

            refreshTokenService.checkRefreshTokenDuplicate(member.getId());

            refreshTokenService.save(refreshToken, member.getId());

            return ResponseEntity.noContent()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("Refresh-Token", "Bearer " + refreshToken)
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 액세스 토큰 재발급 기능 (로그인 유지)
     */
    @PostMapping("/member/refresh")
    public ResponseEntity<?> tokenRefresh(HttpServletRequest request) {
        try {
            if (!validateHeader(request))
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(TOKEN));

            String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
            String refreshToken = request.getHeader("Refresh-Token").substring(7);

            long accessTokenExpireMs = Long.parseLong(myAccessTokenExpireMs);

            String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
            Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();

            refreshTokenService.matches(refreshToken, memberId, mySecretkey);

            String reissuanceAccessToken = JwtTokenUtil.createAccessToken(memberLoginId, mySecretkey, accessTokenExpireMs);

            return ResponseEntity.noContent()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuanceAccessToken)
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 아이디 찾기 기능
     */
    @PostMapping("/member/find-loginid")
    public ResponseEntity<?> findLoginId(@Validated @RequestBody MemberFindLoginIdReqDto memberFindLoginIdReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            Member member = memberService.findMemberByEmail(memberFindLoginIdReqDto.getEmail());

            MemberFindLoginIdResDto memberFindLoginIdResDto = new MemberFindLoginIdResDto(member.getLoginId());

            return ResponseEntity.ok().body(memberFindLoginIdResDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 비밀번호 찾기 기능 (임시 비밀번호 발급)
     */
    @PostMapping("/member/find-password")
    public ResponseEntity<?> findPassword(@Validated @RequestBody MemberFindPasswordReqDto memberFindPasswordReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            Member findMember = memberService.findMemberByLoginIdAndEmail(memberFindPasswordReqDto.getLoginId(), memberFindPasswordReqDto.getEmail());

            memberService.sendMail(memberFindPasswordReqDto, findMember);

            return ResponseEntity.ok().body("임시 비밀번호 발급이 성공 하였습니다. 이메일을 확인해 주세요");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 회원 정보 변경 기능 (현재 비밀번호만 가능)
     */
    @PatchMapping("/member/edit")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @Validated @RequestBody MemberChangePasswordReqDto memberChangePasswordReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);

            memberService.changePassword(findMember, memberChangePasswordReqDto);

            return ResponseEntity.ok().body("비밀번호가 변경 되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 회원 탈퇴 기능
     */
    @DeleteMapping("/member/delete")
    public ResponseEntity<?> memberWithdrawal(HttpServletRequest request) {
        try {
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);

            List<StudyMember> studyMembers = studyMemberService.findStudyMembersWithdrawal(findMember.getId());
            studyMemberService.StudyMembersWithdrawal(studyMembers);
            applicationMemberService.ApplicationMembersWithdrawal(findMember.getId());

            memberService.deleteMember(findMember);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    public boolean validateHeader(HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken = request.getHeader("Refresh-Token");

        if (Objects.isNull(accessToken) || Objects.isNull(refreshToken)) return false;
        else return true;
    }
}
