package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.subject.SubjectReqDto;
import backend.schedule.dto.subject.SubjectResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.Subject;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static backend.schedule.validation.RequestDataValidation.beanValidation;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SubjectController {

    private final SubjectService subjectService;
    private final JwtTokenExtraction jwtTokenExtraction;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 개인 과목 저장 기능
     */
    @PostMapping("/subjects/add")
    public ResponseEntity<?> addSubject(@Validated @RequestBody SubjectReqDto subjectReqDto, BindingResult bindingResult, HttpServletRequest request) {
        try {
            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            Member findMember = jwtTokenExtraction.extractionMember(request, mySecretkey);

            subjectService.save(subjectReqDto, findMember);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 개인 과목 단일 조회 기능
     */
    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<?> findSubject(@PathVariable Long subjectId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            Subject subject = subjectService.findSubjectById(subjectId, memberId);

            return ResponseEntity.ok().body(new SubjectResDto(subject));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 개인 과목 전체 조회 기능 (멤버별)
     */
    @GetMapping("/subjects")
    public ResponseEntity<?> findSubjects(HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            List<SubjectResDto> findSubjects = subjectService.findSubjects(memberId);

            return ResponseEntity.ok().body(new Result(findSubjects));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 개인 과목 변경 기능 (제목)
     */
    @PatchMapping("/subjects/{subjectId}/edit")
    public ResponseEntity<?> updateSubject(@Validated @RequestBody SubjectReqDto subjectReqDto, BindingResult bindingResult,
                                           @PathVariable Long subjectId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            Subject findSubject = subjectService.findSubjectById(subjectId, memberId);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            subjectService.updateSubjectName(findSubject, subjectReqDto);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 개인 과목 삭제 기능
     */
    @DeleteMapping("/subjects/{subjectId}/delete")
    public ResponseEntity<?> subjectDelete(@PathVariable Long subjectId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            Subject findSubject = subjectService.findSubjectById(subjectId, memberId);
            subjectService.deleteSubject(findSubject);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }
}
