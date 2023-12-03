package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.subject.SubjectReqDto;
import backend.schedule.dto.subject.SubjectResDto;
import backend.schedule.dto.Result;
import backend.schedule.entity.Member;
import backend.schedule.entity.Subject;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.MemberService;
import backend.schedule.service.SubjectService;
import backend.schedule.validation.RequestDataValidation;
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
public class SubjectController {

    private final SubjectService subjectService;
    private final MemberService memberService;
    private final JwtTokenExtraction jwtTokenExtraction;
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
    public ResponseEntity<?> subjectAdd(@Validated @RequestBody SubjectReqDto subjectReqDto, BindingResult bindingResult, HttpServletRequest request) {
        try {
            // 빈 검증
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors()
                        .stream()
                        .map(objectError -> objectError.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }


            // 토큰 추출 및 멤버 식별
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);

            // 개인 과목 저장
            SubjectResDto savedSubjectDto = subjectService.save(subjectReqDto, findMember);

            // 응답
            return ResponseEntity.ok().body(savedSubjectDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 개인 과목 단일 조회 기능
     * 요청 데이터 : 개인 과목 id(경로)
     * 요청 횟수 : 1회
     *          1. id값 이용해 개인 과목 조회
     */
    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<?> findSubject(@PathVariable Long subjectId) {
        try {
            // 개인 과목 조회
            Subject subject = subjectService.findOne(subjectId);

            // 응답
            return ResponseEntity.ok().body(new SubjectResDto(subject)); // 개인 과목 카드를 눌렀을 때 출력될 Dto, 기간이나 일정 추가 필요할 듯
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
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
        try {
            // 토큰 추출 및 멤버 식별
            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);

            // 개인 과목 전체 조회 (멤버별)
            List<SubjectResDto> findPersonalSubjects = subjectService.findAll(findMember);

            // 응답
            return ResponseEntity.ok().body(new Result(findPersonalSubjects));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 개인 과목 변경 기능 (제목)
     * 요청 데이터 : 개인 과목 id(경로), 개인 과목 제목
     * 요청 횟수 : 1회
     *          1. 개인 과목 id 이용해 개인 과목 조회
     *          2. 개인 과목 제목 변경
     */
    @PatchMapping("/subjects/{subjectId}/edit")
    public ResponseEntity<?> subjectUpdate(@PathVariable Long subjectId, @Validated @RequestBody SubjectReqDto subjectReqDto, BindingResult bindingResult) {
        try {
            // 빈 검증
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors()
                        .stream()
                        .map(objectError -> objectError.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            // 개인 과목 변경 (제목)
            subjectService.subjectNameUpdate(subjectId, subjectReqDto);

            // 응답
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
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
    public ResponseEntity<?> subjectDelete(@PathVariable Long subjectId) {
        try {
            Subject findSubject = subjectService.findOne(subjectId);
            // 개인 과목 삭제
            subjectService.deleteSubject(findSubject);

            // 응답
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }
}
