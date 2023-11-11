package backend.schedule.controller;

import backend.schedule.dto.PersonalSubjectDto;
import backend.schedule.dto.PersonalSubjectResDto;
import backend.schedule.dto.PersonalSubjectsResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.PersonalSubject;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.MemberService;
import backend.schedule.service.PersonalSubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PersonalSubjectController {

    private final PersonalSubjectService personalSubjectService;
    private final MemberService memberService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    @PostMapping("/subjects/add")
    public ResponseEntity<?> subjectAdd(@Validated @RequestBody PersonalSubjectDto personalSubjectDto, BindingResult bindingResult, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);

        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);

        personalSubjectDto.setMember(findMember);

        personalSubjectService.save(personalSubjectDto);

        return ResponseEntity.ok("과목이 추가 되었습니다.");
    }

    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<?> findSubject(@PathVariable Long subjectId) {
        PersonalSubject personalSubject = personalSubjectService.findOne(subjectId);

        if (personalSubject == null) {
            return ResponseEntity.notFound().build();
        }
        PersonalSubjectResDto personalSubjectResDto = new PersonalSubjectResDto();
        personalSubjectResDto.setSubjectName(personalSubject.getSubjectName());

        return ResponseEntity.ok(personalSubjectResDto); // 개인 과목 카드를 눌렀을 때 출력될 Dto, 기간이나 일정 추가 필요할 듯
    }

    @GetMapping("/subjects")
    public ResponseEntity<?> findSubjects(HttpServletRequest httpServletRequest) {
        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);

        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);

        PersonalSubjectsResDto findPersonalSubjects = personalSubjectService.findAll(findMember);

        return ResponseEntity.ok(findPersonalSubjects);
    }

    @PatchMapping("/subjects/{subjectId}/edit")
    public ResponseEntity<?> subjectUpdate(@PathVariable Long subjectId, @RequestBody PersonalSubjectDto personalSubjectDto) {
        personalSubjectService.subjectNameUpdate(subjectId, personalSubjectDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/subjects/{subjectId}/delete")
    public ResponseEntity<?> subjectDelete(@PathVariable Long subjectId) {
        personalSubjectService.subjectDelete(subjectId);
        return ResponseEntity.ok().build();
    }
}
