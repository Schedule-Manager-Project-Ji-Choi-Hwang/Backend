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

    /**
     * 회원 가입 기능
     * 요청 데이터 : 로그인 아이디, 비밀번호, 비밀번호 재확인, 닉네임, 이메일
     * 요청 횟수 : 3회
     *          1. 로그인 아이디 중복 체크
     *          2. 닉네임 중복 체크
     *          3. 회원 저장
     */
    @PostMapping("/member/sign-up")
    public ResponseEntity<?> join(@Validated @RequestBody MemberJoinReqDto memberJoinReqDto, BindingResult bindingResult) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        // 로그인 아이디 및 닉네임 중복 검증
        if (memberService.checkLoginIdDuplicate(memberJoinReqDto.getLoginId())) {
            return ResponseEntity.badRequest().body("로그인 아이디가 중복됩니다.");
        } else if (memberService.checkNicknameDuplicate(memberJoinReqDto.getNickname())) {
            return ResponseEntity.badRequest().body("닉네임이 중복됩니다.");
        }

        // 회원 가입
        memberService.join(memberJoinReqDto);

        // 응답
        return ResponseEntity.ok().body("회원가입 성공");
    }

    /**
     * 로그인 기능
     * 요청 데이터 : 로그인 아이디, 비밀번호
     * 요청 횟수 : 2회
     *          1. loginId 이용 멤버 조회
     *          2. Refresh 토큰 저장
     */
    @PostMapping("/member/log-in")
    public ResponseEntity<?> login(@Validated @RequestBody MemberLoginReqDto memberLoginReqDto, BindingResult bindingResult) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        // 로그인 아이디 및 비밀번호 통해 멤버 식별 (로그인)
        Member member = memberService.login(memberLoginReqDto);
        if (member == null) {
            return ResponseEntity.badRequest().body("로그인 아이디 또는 비밀번호가 잘못되었습니다.");
        }

        // 시크릿 키 및 토큰 만료 기한 가져오기
        String secretKey = mySecretkey;
        long accessTokenExpireMs = Long.parseLong(myAccessTokenExpireMs);
        long refreshTokenExpireMs = Long.parseLong(myRefreshTokenExpireMs);

        // 토큰 생성
        String accessToken = JwtTokenUtil.createAccessToken(member.getLoginId(), secretKey, accessTokenExpireMs);
        String refreshToken = JwtTokenUtil.createRefreshToken(secretKey, refreshTokenExpireMs);

        // Refresh 토큰 저장
        refreshTokenService.save(refreshToken, member.getId());

        // 응답
        return ResponseEntity.noContent()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("Refresh-Token", "Bearer " + refreshToken)
                .build(); // 프론트에서 헤더 안받아짐.
    }

    /**
     * 액세스 토큰 재발급 기능 (로그인 유지)
     * 요청 데이터 : AccessToken(헤더), RefreshToken(헤더)
     * 요청 횟수 : 2회
     *          1. 로그인 아이디 이용 멤버 조회
     *          2. 해당 회원의 DB Refresh 토큰 조회
     */
    @GetMapping("/member/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        // 요청 헤더의 토큰 포함 여부 확인
        if (!validateHeader(request)) {
            return ResponseEntity.badRequest().body("토큰을 찾을 수 없습니다.");
        }

        // 요청 헤더의 토큰 추출
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String refreshToken = request.getHeader("Refresh-Token").substring(7);

        // 시크릿 키 및 만료 기한 가져오기
        String secretKey = mySecretkey;
        long accessTokenExpireMs = Long.parseLong(myAccessTokenExpireMs);

        // 토큰 정보 이용해 멤버 식별
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();

        // 요청 Refresh토큰과 DB Refresh 토큰 일치 여부 확인 및 만료기한 검사
        boolean matches = refreshTokenService.matches(refreshToken, memberId, secretKey);
        if (!matches) {
            return ResponseEntity.badRequest().body("토큰이 올바르지 않습니다."); // 사용자를 재 로그인 시켜야 함.
        }

        // 재발급 할 Access토큰 생성
        String reissuanceAccessToken = JwtTokenUtil.createAccessToken(memberLoginId, secretKey, accessTokenExpireMs);

        // 응답
        return ResponseEntity.noContent()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + reissuanceAccessToken)
                .build();
    }

    /**
     * 아이디 찾기 기능
     * 요청 데이터 : 이메일
     * 요청 횟수 : 1회
     *          1. 이메일 이용해 멤버 조회
     */
    @GetMapping("/member/findLoginId")
    public ResponseEntity<?> findLoginId(@Validated @RequestBody FindLoginIdReqDto findLoginIdReqDto, BindingResult bindingResult) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        // 멤버 조회 및 아이디값 조회
        Member member = memberService.findMemberByEmail(findLoginIdReqDto.getEmail());
        if (member == null) {
            return ResponseEntity.badRequest().body("해당 이메일로 가입된 회원이 없습니다.");
        }

        // 반환할 Dto 생성
        FindLoginIdResDto findLoginIdResDto = new FindLoginIdResDto(member.getLoginId());

        // 응답
        return ResponseEntity.ok().body(findLoginIdResDto);
    }

    /**
     * 비밀번호 찾기 기능 (임시 비밀번호 발급)
     * 요청 횟수 : 2회
     *          1. 로그인 아이디 및 이메일 이용해 멤버 조회
     *          2. 멤버 비밀번호 변경 (임시 비밀번호)
     */
    @PostMapping("/member/findPassword")
    public ResponseEntity<?> findPassword(@Validated @RequestBody FindPasswordReqDto findPasswordReqDto, BindingResult bindingResult) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        // 멤버 식별
        Member findMember = memberService.findMemberByLoginIdAndEmail(findPasswordReqDto.getLoginId(), findPasswordReqDto.getEmail());
        if (findMember == null) {
            return ResponseEntity.badRequest().body("아이디 및 이메일을 다시 확인해 주세요");
        }

        // 이메일 발송
        String result = memberService.sendMail(findPasswordReqDto, findMember);
        if (result.equals("fail")) {
            return ResponseEntity.badRequest().body("임시 비밀번호 발급이 실패하였습니다.");
        }

        // 응답
        return ResponseEntity.ok().body("임시 비밀번호 발급이 성공 하였습니다. 이메일을 확인해 주세요");
    }

    /**
     * 회원 정보 변경 기능 (현재 비밀번호만 가능)
     * 요청 데이터 : AccessToken(헤더), 비밀번호
     * 요청 횟수 : 2회
     *          1. 로그인 아이디 이용해 멤버 조회
     *          2. 멤버 비밀번호 변경
     */
    @PatchMapping("/member/edit")
    public ResponseEntity<?> changePW(HttpServletRequest request, @Validated @RequestBody MemberPasswordReqDto memberPasswordReqDto, BindingResult bindingResult) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        // 토큰 추출 및 멤버 식별
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);
        if (findMember == null) {
            return ResponseEntity.badRequest().body("해당 회원을 찾을 수 없습니다.");
        }

        // 비밀번호 변경
        memberService.changePW(findMember, memberPasswordReqDto);

        // 응답
        return ResponseEntity.ok().body("비밀번호가 변경 되었습니다."); // 변경 완료 시 재로그인 시켜야함.
    }

    // 요청 헤더의 토큰 포함 여부 확인 메서드
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
