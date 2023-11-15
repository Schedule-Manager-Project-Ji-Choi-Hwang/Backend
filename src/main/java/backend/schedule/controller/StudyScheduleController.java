package backend.schedule.controller;


import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.StudyPostScheduleSetDto;
import backend.schedule.dto.StudyScheduleDto;
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
     * 스터디 일정 CRUD
     */
//    @GetMapping("/studyboard/{boardId}/study-schedule/add")
//    public StudyScheduleDto studyScheduleForm(@RequestBody StudyScheduleDto scheduleDto) {
//        return scheduleDto;
//    }
    @Transactional
    @PostMapping("/studyboard/{boardId}/study-schedule/add")
    public ResponseEntity<?> studyScheduleAdd(@Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult, @PathVariable Long boardId) {
        StudyPost findPost = studyPostService.findById(boardId);

        if (findPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        }

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        StudySchedule studySchedule = new StudySchedule(scheduleDto);
        findPost.addStudySchedule(studySchedule);

        return ResponseEntity.ok().build();
    }

    @GetMapping({"/studyboard/{boardId}/study-schedule/{id}/edit", "/studyboard/{boardId}/study-schedule/{id}"})
    //아무나 일정을 볼 수 있는 문제있음
    public ResponseEntity<?> studyScheduleUpdateForm(@PathVariable Long id) {
        StudySchedule findSchedule = studyScheduleService.findById(id);

        if (findSchedule == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SCHEDULE));
        }

        StudyScheduleDto studyScheduleDto = new StudyScheduleDto(findSchedule);

        return ResponseEntity.ok().body(studyScheduleDto);
    }

    @Transactional
    @PatchMapping("/studyboard/{boardId}/study-schedule/{id}/edit")
    public ResponseEntity<?> studyScheduleUpdate(
            @Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult,
            @PathVariable Long id, @PathVariable Long boardId) {

        StudySchedule findSchedule = studyScheduleService.findById(id);

        if (findSchedule == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SCHEDULE));
        }

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        findSchedule.updateSchedule(scheduleDto.getScheduleName(), scheduleDto.getPeriod());

        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/studyboard/{boardId}/study-schedule/{id}/delete")
    public ResponseEntity<?> studyScheduleDelete(@PathVariable Long boardId, @PathVariable Long id) {
        StudyPost findPost = studyPostService.findById(boardId);
        StudySchedule findSchedule = studyScheduleService.findById(id);

        if (findPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        } else if (findSchedule == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SCHEDULE));
        }

        findPost.removeStudySchedule(findSchedule);
        //쿼리 4번 개선방법 생각
        return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
    }

    @GetMapping("/studyboard/{boardId}/study-schedules")
    public ResponseEntity<Result> studyScheduleList(@PathVariable Long boardId) {
        StudyPost studyPost = studyPostService.studyScheduleList(boardId);//optional 쓸 수 있는지 해보기

        return ResponseEntity.ok().body(new Result(new StudyPostScheduleSetDto(studyPost)));
    }
}
