package backend.schedule.controller;

import backend.schedule.dto.MemberJoinDto;
import backend.schedule.dto.MemberLoginDto;
import backend.schedule.entity.Member;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

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

        String secretKey = "secret-key-456456";
        long expireTimeMs = 1000* 60* 60;

        String jwtToken = JwtTokenUtil.createToken(member.getLoginId(), secretKey, expireTimeMs);

        return ResponseEntity.ok(jwtToken);
    }

    @GetMapping("/test")
    public String test() {
        return "잘되는듯";
    }
}
