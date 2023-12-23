package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.schedule.ScheduleDto;
import backend.schedule.dto.schedule.ScheduleEditReqDto;
import backend.schedule.dto.schedule.ScheduleReqDto;
import backend.schedule.entity.Schedule;
import backend.schedule.entity.Subject;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.ScheduleService;
import backend.schedule.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static backend.schedule.validation.RequestDataValidation.beanValidation;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final SubjectService subjectService;
    private final ScheduleService scheduleService;
    private final JwtTokenExtraction jwtTokenExtraction;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스케쥴 저장 기능
     */
    @PostMapping("/subjects/{subjectId}/schedules/add")
    public ResponseEntity<?> addSchedule(@Validated @RequestBody ScheduleReqDto scheduleReqDto, BindingResult bindingResult,
                                         @PathVariable Long subjectId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            Subject findSubject = subjectService.findSubjectById(subjectId, memberId);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            scheduleService.addSchedule(scheduleReqDto, findSubject);

            return ResponseEntity.ok().body("일정 등록 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스케쥴 단일 조회 기능
     */
    @GetMapping("/subjects/{subjectId}/schedules/{scheduleId}")
    public ResponseEntity<?> findSchedule(@PathVariable Long subjectId, @PathVariable Long scheduleId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            subjectService.findSubjectById(subjectId, memberId);

            Schedule findSchedule = scheduleService.findScheduleById(scheduleId, subjectId);

            return ResponseEntity.ok().body(new ScheduleDto(findSchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스케쥴 변경 기능 (제목, 기간(period))
     */
    @PatchMapping("/subjects/{subjectId}/schedules/{scheduleId}/edit")
    public ResponseEntity<?> updateSchedule(@Validated @RequestBody ScheduleEditReqDto scheduleEditReqDto, BindingResult bindingResult,
                                            @PathVariable Long subjectId, @PathVariable Long scheduleId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            subjectService.findSubjectById(subjectId, memberId);

            Schedule findSchedule = scheduleService.findScheduleById(scheduleId, subjectId);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            scheduleService.updateSchedule(findSchedule, scheduleEditReqDto);

            return ResponseEntity.ok("변경 되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스케쥴 삭제 기능
     */
    @DeleteMapping("/subjects/{subjectId}/schedules/{scheduleId}/delete")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long subjectId, @PathVariable Long scheduleId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            subjectService.findSubjectById(subjectId, memberId);

            Schedule findSchedule = scheduleService.findScheduleById(scheduleId, subjectId);

            scheduleService.deleteSchedule(findSchedule);

            return ResponseEntity.ok("삭제 되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }
}
