package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.subject.PersonalSubjectReqDto;
import backend.schedule.dto.subject.PersonalSubjectResDto;
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

import static backend.schedule.enumlist.ErrorMessage.*;

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
     * 요청 데이터 : AccessToken(헤더), 과목 이름
     * 요청 횟수 : 2회
     *          1. 로그인 아이디 이용해 멤버 조회
     *          2. 개인 과목 저장
     */
    @PostMapping("/subjects/add")
    public ResponseEntity<?> subjectAdd(@Validated @RequestBody PersonalSubjectReqDto personalSubjectReqDto, BindingResult bindingResult, HttpServletRequest httpServletRequest) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        // 토큰 추출 및 멤버 식별
        String accessToken = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);
        if (findMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(MEMBER));
        }

        // 개인 과목 저장
        PersonalSubjectResDto savedSubjectDto = personalSubjectService.save(personalSubjectReqDto, findMember);

        // 응답
        return ResponseEntity.ok().body(savedSubjectDto);
    }

    /**
     * 개인 과목 단일 조회 기능
     * 요청 데이터 : 개인 과목 id(경로)
     * 요청 횟수 : 1회
     *          1. id값 이용해 개인 과목 조회
     */
    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<?> findSubject(@PathVariable Long subjectId) {
        // 개인 과목 조회
        PersonalSubject personalSubject = personalSubjectService.findOne(subjectId);
        if (personalSubject == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SUBJECT));
        }

        // 응답
        return ResponseEntity.ok().body(new PersonalSubjectResDto(personalSubject)); // 개인 과목 카드를 눌렀을 때 출력될 Dto, 기간이나 일정 추가 필요할 듯
    }

    /**
     * 개인 과목 전체 조회 기능 (멤버별)
     * 요청 데이터 : ''
     * 요청 횟수 : 2회
     *          1. 로그인 아이디 이용해 멤버 조회
     *          2. 멤버 객체 이용해 개인 과목들 조회
     */
    @GetMapping("/subjects")
    public ResponseEntity<?> findSubjects(HttpServletRequest request) {
        // 토큰 추출 및 멤버 식별
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);
        if (findMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(MEMBER));
        }

        // 개인 과목 전체 조회 (멤버별)
        List<PersonalSubjectResDto> findPersonalSubjects = personalSubjectService.findAll(findMember);

        // 응답
        return ResponseEntity.ok().body(new Result(findPersonalSubjects));
    }

    /**
     * 개인 과목 변경 기능 (제목)
     * 요청 데이터 : 개인 과목 id(경로), 개인 과목 제목
     * 요청 횟수 : 1회
     *          1. 개인 과목 id 이용해 개인 과목 조회
     *          2. 개인 과목 제목 변경
     */
    @PatchMapping("/subjects/{subjectId}/edit")
    public ResponseEntity<?> subjectUpdate(@PathVariable Long subjectId, @Validated @RequestBody PersonalSubjectReqDto personalSubjectReqDto, BindingResult bindingResult) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        // 개인 과목 변경 (제목)
        personalSubjectService.subjectNameUpdate(subjectId, personalSubjectReqDto);
        
        // 응답
        return ResponseEntity.ok().build();
    }

    /**
     * 개인 과목 삭제 기능
     * 요청 데이터 : 개인 과목 id(경로)
     * 요청 횟수 : 4회
     *          1. 멤버 조회
     *          2. 개인 과목 조회
     *          3. 멤버 id로 개인 과목 조회
     *          4. 개인 과목 삭제
     */
    @DeleteMapping("/subjects/{subjectId}/delete")
    public ResponseEntity<?> subjectDelete(HttpServletRequest request, @PathVariable Long subjectId) {
        // 토큰 추출 및 멤버 식별
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);
        if (findMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(MEMBER));
        }

        PersonalSubject findSubject = personalSubjectService.findOne(subjectId);
        if (findSubject == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SUBJECT));
        }

        // 개인 과목 삭제
        personalSubjectService.subjectDelete(findMember, findSubject);
        
        // 응답
        return ResponseEntity.ok().build();
    }
}
