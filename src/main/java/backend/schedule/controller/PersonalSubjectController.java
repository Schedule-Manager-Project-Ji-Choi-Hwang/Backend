package backend.schedule.controller;

import backend.schedule.dto.PersonalSubjectDto;
import backend.schedule.dto.PersonalSubjectResDto;
import backend.schedule.dto.Result;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PersonalSubjectController {

    private final PersonalSubjectService personalSubjectService;
    private final MemberService memberService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 개인 과목 저장 기능
     * 요청 횟수 : 회
     */
    @PostMapping("/subjects/add")
    public ResponseEntity<?> subjectAdd(@Validated @RequestBody PersonalSubjectDto personalSubjectDto, BindingResult bindingResult, HttpServletRequest httpServletRequest) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }

        // 토큰 추출 및 멤버 식별
        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);

        // 개인 과목 저장
        personalSubjectService.save(personalSubjectDto, findMember);

        // 응답
        return ResponseEntity.ok().body("과목이 추가 되었습니다.");
    }

    /**
     * 개인 과목 단일 조회 기능
     * 요청 횟수 : 회
     */
    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<?> findSubject(@PathVariable Long subjectId) {
        // 개인 과목 조회
        PersonalSubject personalSubject = personalSubjectService.findOne(subjectId);
        if (personalSubject == null) {
            return ResponseEntity.badRequest().body("과목을 찾을 수 없습니다.");
        }

        // 응답
        return ResponseEntity.ok().body(new PersonalSubjectResDto(personalSubject)); // 개인 과목 카드를 눌렀을 때 출력될 Dto, 기간이나 일정 추가 필요할 듯
    }

    /**
     * 개인 과목 전체 조회 기능 (멤버별)
     * 요청 횟수 : 회
     */
    @GetMapping("/subjects")
    public ResponseEntity<?> findSubjects(HttpServletRequest httpServletRequest) {
        // 토큰 추출 및 멤버 식별
        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);

        // 개인 과목 전체 조회 (멤버별)
        List<PersonalSubjectResDto> findPersonalSubjects = personalSubjectService.findAll(findMember);

        // 응답
        return ResponseEntity.ok().body(new Result(findPersonalSubjects));
    }

    /**
     * 개인 과목 변경 기능 (제목)
     * 요청 횟수 : 회
     */
    @PatchMapping("/subjects/{subjectId}/edit")
    public ResponseEntity<?> subjectUpdate(@PathVariable Long subjectId, @RequestBody PersonalSubjectDto personalSubjectDto) {
        // 개인 과목 변경 (제목)
        personalSubjectService.subjectNameUpdate(subjectId, personalSubjectDto);
        
        // 응답
        return ResponseEntity.ok().build();
    }

    /**
     * 개인 과목 삭제 기능
     * 요청 횟수 : 회
     */
    @DeleteMapping("/subjects/{subjectId}/delete")
    public ResponseEntity<?> subjectDelete(@PathVariable Long subjectId) {
        // 개인 과목 삭제
        personalSubjectService.subjectDelete(subjectId);
        
        // 응답
        return ResponseEntity.ok().build();
    }
}
