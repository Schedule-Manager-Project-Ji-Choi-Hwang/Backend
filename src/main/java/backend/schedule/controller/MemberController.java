package backend.schedule.controller;

import backend.schedule.dto.*;
import backend.schedule.entity.Member;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.MemberService;
import backend.schedule.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final RefreshTokenService refreshTokenService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    @Value("${spring.jwt.token.access.expire}")
    private String myAccessTokenExpireMs;

    @Value("${spring.jwt.token.refresh.expire}")
    private String myRefreshTokenExpireMs;

    @PostMapping("/member/sign-up")
    public ResponseEntity<?> join(@Validated @RequestBody MemberJoinDto memberJoinDto, BindingResult bindingResult) {

        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        //로그인 아이디 및 닉네임 중복 검증
        if (memberService.checkLoginIdDuplicate(memberJoinDto.getLoginId())) {
            return ResponseEntity.badRequest().body("로그인 아이디가 중복됩니다.");
        } else if (memberService.checkNicknameDuplicate(memberJoinDto.getNickname())) {
            return ResponseEntity.badRequest().body("닉네임이 중복됩니다.");
        }


        memberService.join(memberJoinDto);

        return ResponseEntity.ok().body("회원가입 성공");
    }

    @PostMapping("/member/log-in")
    public ResponseEntity<?> login(@Validated @RequestBody MemberLoginDto memberLoginDto, BindingResult bindingResult) {

        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        Member member = memberService.login(memberLoginDto);

        if (member == null) {
            return ResponseEntity.badRequest().body("로그인 아이디 또는 비밀번호가 잘못되었습니다.");
        }

        String secretKey = mySecretkey;
        long accessTokenExpireMs = Long.parseLong(myAccessTokenExpireMs);
        long refreshTokenExpireMs = Long.parseLong(myRefreshTokenExpireMs);

        String accessToken = JwtTokenUtil.createAccessToken(member.getLoginId(), secretKey, accessTokenExpireMs);
        String refreshToken = JwtTokenUtil.createRefreshToken(secretKey, refreshTokenExpireMs);

        refreshTokenService.save(refreshToken, member.getId());

        return ResponseEntity.noContent()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("Refresh-Token", "Bearer " + refreshToken)
                .build(); // 프론트에서 헤더 안받아짐.
    }

    @GetMapping("/member/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        if (!validateHeader(request)) {
            return ResponseEntity.badRequest().body("토큰을 찾을 수 없습니다.");
        }

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String refreshToken = request.getHeader("Refresh-Token").substring(7);

        String secretKey = mySecretkey;
        long accessTokenExpireMs = Long.parseLong(myAccessTokenExpireMs);
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();


        boolean matches = refreshTokenService.matches(refreshToken, memberId, secretKey);

        if (!matches) {
            return ResponseEntity.badRequest().body("토큰이 올바르지 않습니다."); // 사용자를 재 로그인 시켜야 함.
        }

        String reissuanceAccessToken = JwtTokenUtil.createAccessToken(memberLoginId, secretKey, accessTokenExpireMs);

        return ResponseEntity.noContent()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuanceAccessToken)
                .build();
    }

    @GetMapping("/member/findLoginId")
    public ResponseEntity<?> findLoginId(@Validated @RequestBody FindLoginIdReqDto findLoginIdReqDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        FindLoginIdResDto findLoginIdResDto = memberService.findLoginId(findLoginIdReqDto.getEmail()); // isEmpty랑 null 반환 방식으로, null 검증

        //badRequest 검증 추가 (null 일 때)

        return ResponseEntity.ok().body(findLoginIdResDto);
    }

    @PostMapping("/member/findPassword")
    public ResponseEntity<?> findPassword(@Validated @RequestBody FindPasswordReqDto findPasswordReqDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        if (!memberService.checkLoginIdAndEmail(findPasswordReqDto.getLoginId(), findPasswordReqDto.getEmail())) {
            return ResponseEntity.badRequest().body("아이디 및 이메일을 다시 확인해 주세요");
        }

        EmailMessageDto emailMessageDto = EmailMessageDto.builder()
                .to(findPasswordReqDto.getEmail())
                .subject("[일정관리 앱] 임시 비밀번호 발급")
                .build();

        String result = memberService.sendMail(emailMessageDto, "password");

        if (result.equals("fail")) {
            return ResponseEntity.badRequest().body("임시 비밀번호 발급이 실패하였습니다.");
        }

        return ResponseEntity.ok().body("임시 비밀번호 발급이 성공 하였습니다. 이메일을 확인해 주세요");
    }

    @PatchMapping("/member/edit")
    public ResponseEntity<?> changePW(HttpServletRequest request, @Validated @RequestBody MemberPWDto memberPWDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);

        if (findMember == null) {
            return ResponseEntity.badRequest().body("해당 회원을 찾을 수 없습니다.");
        }

        memberService.changePW(findMember, memberPWDto);

        return ResponseEntity.ok().body("비밀번호가 변경 되었습니다."); // 변경 완료 시 재로그인 시켜야함.
    }


    public boolean validateHeader(HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken = request.getHeader("Refresh-Token");
        if (Objects.isNull(accessToken) || Objects.isNull(refreshToken)) {
            return false;
        } else {
            return true;
        }
    }
}
