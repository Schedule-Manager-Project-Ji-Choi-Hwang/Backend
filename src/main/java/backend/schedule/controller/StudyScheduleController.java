package backend.schedule.controller;


import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.studyschedule.StudyPostScheduleSetDto;
import backend.schedule.dto.studyschedule.StudyScheduleDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class StudyScheduleController {

    private final StudyPostService studyPostService;
    private final StudyScheduleService studyScheduleService;

    /**
     * 스터디 일정 추가
     * Query: 2번
     */
    @Transactional
    @PostMapping("/studyboard/{studyBoardId}/study-schedule/add")
    public ResponseEntity<?> studyScheduleAdd(@Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult, @PathVariable Long studyBoardId) {

        try {
            StudyPost findPost = studyPostService.findById(studyBoardId);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            StudySchedule studySchedule = new StudySchedule(scheduleDto);
            findPost.addStudySchedule(studySchedule);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 일정 조회
     * Query: 1번
     */
    @GetMapping({"/studyboard/{studyBoardId}/study-schedule/{studyScheduleId}/edit", "/studyboard/{studyBoardId}/study-schedule/{studyScheduleId}"})
    //아무나 일정을 볼 수 있는 문제있음
    public ResponseEntity<?> studyScheduleUpdateForm(@PathVariable Long studyScheduleId) {

        try {
            StudySchedule findSchedule = studyScheduleService.findById(studyScheduleId);
            StudyScheduleDto studyScheduleDto = new StudyScheduleDto(findSchedule);

            return ResponseEntity.ok().body(studyScheduleDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 일정 전체조회
     * Query: Fetch join이용 1번
     */
    @GetMapping("/studyboard/{studyBoardId}/study-schedules")
    public ResponseEntity<?> studyScheduleList(@PathVariable Long studyBoardId) {

        try {
            StudyPost studyPost = studyPostService.studyScheduleList(studyBoardId);

            return ResponseEntity.ok().body(new Result(new StudyPostScheduleSetDto(studyPost)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 일정 수정
     * Query: 2번
     */
    @Transactional
    @PatchMapping("/studyboard/{studyBoardId}/study-schedule/{studyScheduleId}/edit")
    public ResponseEntity<?> studyScheduleUpdate(
            @Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult, @PathVariable Long studyScheduleId) {

        try {
            StudySchedule findSchedule = studyScheduleService.findById(studyScheduleId);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            findSchedule.updateSchedule(scheduleDto.getScheduleName(), scheduleDto.getPeriod());

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 일정 삭제
     * Query: 3번
     */
    @Transactional
    @DeleteMapping("/studyboard/{studyBoardId}/study-schedule/{studyScheduleId}/delete")
    public ResponseEntity<?> studyScheduleDelete(@PathVariable Long studyBoardId, @PathVariable Long studyScheduleId) {

        try {
            StudyPost findPost = studyPostService.findById(studyBoardId);
            StudySchedule findSchedule = studyScheduleService.findById(studyScheduleId);

            findPost.removeStudySchedule(findSchedule);
            //쿼리 4번 개선방법 생각
            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}
