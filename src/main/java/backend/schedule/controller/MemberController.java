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
     * 요청 데이터 : 로그인 아이디, 비밀번호, 비밀번호 재확인, 닉네임, 이메일
     * 요청 횟수 : 3회
     * 1. 로그인 아이디 중복 체크
     * 2. 닉네임 중복 체크
     * 3. 회원 저장
     */
    @PostMapping("/member/sign-up")
    public ResponseEntity<?> join(@Validated @RequestBody MemberJoinReqDto memberJoinReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            // 로그인 아이디 및 닉네임 중복 검증
            memberService.checkLoginIdDuplicate(memberJoinReqDto.getLoginId());
            memberService.checkNicknameDuplicate(memberJoinReqDto.getNickname());
            memberService.checkEmailDuplicate(memberJoinReqDto.getEmail());

            // 회원 가입
            memberService.join(memberJoinReqDto);

            // 응답
            return ResponseEntity.ok().body("회원가입 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 로그인 기능
     * 요청 데이터 : 로그인 아이디, 비밀번호
     * 요청 횟수 : 2회
     * 1. loginId 이용 멤버 조회
     * 2. Refresh 토큰 저장
     */
    @PostMapping("/member/log-in")
    public ResponseEntity<?> login(@Validated @RequestBody MemberLoginReqDto memberLoginReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            // 로그인 아이디 및 비밀번호 통해 멤버 식별 (로그인)
            Member member = memberService.login(memberLoginReqDto);

            // 토큰 만료 기한 가져오기
            long accessTokenExpireMs = Long.parseLong(myAccessTokenExpireMs);
            long refreshTokenExpireMs = Long.parseLong(myRefreshTokenExpireMs);

            // 토큰 생성
            String accessToken = JwtTokenUtil.createAccessToken(member.getLoginId(), mySecretkey, accessTokenExpireMs);
            String refreshToken = JwtTokenUtil.createRefreshToken(mySecretkey, refreshTokenExpireMs);

            // 여기
            refreshTokenService.checkRefreshTokenDuplicate(member.getId());

            // Refresh 토큰 저장
            refreshTokenService.save(refreshToken, member.getId());

            // 응답
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
     * 요청 데이터 : AccessToken(헤더), RefreshToken(헤더)
     * 요청 횟수 : 2회
     * 1. 로그인 아이디 이용 멤버 조회
     * 2. 해당 회원의 DB Refresh 토큰 조회
     */
    @GetMapping("/member/refresh")
    public ResponseEntity<?> tokenRefresh(HttpServletRequest request) {
        try {
            // 요청 헤더의 토큰 포함 여부 확인
            if (!validateHeader(request))
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(TOKEN));

            // 요청 헤더의 토큰 추출
            String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
            String refreshToken = request.getHeader("Refresh-Token").substring(7);

            // 만료 기한 가져오기
            long accessTokenExpireMs = Long.parseLong(myAccessTokenExpireMs);

            // 토큰 정보 이용해 멤버 식별
            String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
            Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();

            // 요청 Refresh토큰과 DB Refresh 토큰 일치 여부 확인 및 만료기한 검사
            refreshTokenService.matches(refreshToken, memberId, mySecretkey);

            // 재발급 할 Access토큰 생성
            String reissuanceAccessToken = JwtTokenUtil.createAccessToken(memberLoginId, mySecretkey, accessTokenExpireMs);

            // 응답
            return ResponseEntity.noContent()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuanceAccessToken)
                    .build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 아이디 찾기 기능
     * 요청 데이터 : 이메일
     * 요청 횟수 : 1회
     * 1. 이메일 이용해 멤버 조회
     */
    @PostMapping("/member/find-loginid")
    public ResponseEntity<?> findLoginId(@Validated @RequestBody MemberFindLoginIdReqDto memberFindLoginIdReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            // 멤버 조회 및 아이디값 조회
            Member member = memberService.findMemberByEmail(memberFindLoginIdReqDto.getEmail());

            // 반환할 Dto 생성
            MemberFindLoginIdResDto memberFindLoginIdResDto = new MemberFindLoginIdResDto(member.getLoginId());

            // 응답
            return ResponseEntity.ok().body(memberFindLoginIdResDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 비밀번호 찾기 기능 (임시 비밀번호 발급)
     * 요청 횟수 : 2회
     * 1. 로그인 아이디 및 이메일 이용해 멤버 조회
     * 2. 멤버 비밀번호 변경 (임시 비밀번호)
     */
    @PostMapping("/member/find-password")
    public ResponseEntity<?> findPassword(@Validated @RequestBody MemberFindPasswordReqDto memberFindPasswordReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            // 멤버 식별
            Member findMember = memberService.findMemberByLoginIdAndEmail(memberFindPasswordReqDto.getLoginId(), memberFindPasswordReqDto.getEmail());

            // 이메일 발송
            memberService.sendMail(memberFindPasswordReqDto, findMember);

            // 응답
            return ResponseEntity.ok().body("임시 비밀번호 발급이 성공 하였습니다. 이메일을 확인해 주세요");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 회원 정보 변경 기능 (현재 비밀번호만 가능)
     * 요청 데이터 : AccessToken(헤더), 비밀번호
     * 요청 횟수 : 2회
     * 1. 로그인 아이디 이용해 멤버 조회
     * 2. 멤버 비밀번호 변경
     */
    @PatchMapping("/member/edit")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @Validated @RequestBody MemberChangePasswordReqDto memberChangePasswordReqDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            // 토큰 추출 및 멤버 식별
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);

            // 비밀번호 변경
            memberService.changePassword(findMember, memberChangePasswordReqDto);

            // 응답
            return ResponseEntity.ok().body("비밀번호가 변경 되었습니다."); // 변경 완료 시 재로그인 시켜야함.
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 회원 탈퇴 기능
     * 요청 데이터 : AccessToken(헤더)
     * 요청 횟수 : 회
     */

    @DeleteMapping("/member/delete")
    public ResponseEntity<?> memberWithdrawal(HttpServletRequest request) {
        try {
            // 토큰 추출 및 멤버 식별
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);

            List<StudyMember> studyMembers = studyMemberService.findStudyMembersWithdrawal(findMember.getId());

            studyMemberService.StudyMembersWithdrawal(studyMembers);

            // 신청 회원 조회 및 삭제
            applicationMemberService.ApplicationMembersWithdrawal(findMember.getId());

            // 멤버 삭제
            memberService.deleteMember(findMember);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    // 요청 헤더의 토큰 포함 여부 확인 메서드
    public boolean validateHeader(HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken = request.getHeader("Refresh-Token");

        if (Objects.isNull(accessToken) || Objects.isNull(refreshToken)) return false;
        else return true;
    }
}
