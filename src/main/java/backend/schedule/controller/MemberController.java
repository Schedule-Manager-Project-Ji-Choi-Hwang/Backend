package backend.schedule.controller;

import backend.schedule.dto.MemberJoinDto;
import backend.schedule.dto.MemberLoginDto;
import backend.schedule.entity.Member;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.MemberService;
import backend.schedule.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> join(@Valid @RequestBody MemberJoinDto memberJoinDto, BindingResult bindingResult) {

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

        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/member/log-in")
    public ResponseEntity<?> login(@Valid @RequestBody MemberLoginDto memberLoginDto, BindingResult bindingResult) {

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
                .build();
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
