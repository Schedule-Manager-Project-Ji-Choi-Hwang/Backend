package backend.schedule.controller;

import backend.schedule.dto.MemberJoinDto;
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

    @GetMapping("/mmm")
    public String aaa() {
        return "hhello!";
    }
}
