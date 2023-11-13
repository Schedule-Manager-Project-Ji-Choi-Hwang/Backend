package backend.schedule.controller;


import backend.schedule.dto.Result;
import backend.schedule.dto.StudyPostScheduleSetDto;
import backend.schedule.dto.StudyScheduleDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StudyScheduleController {

    private final StudyPostService studyPostService;
    private final StudyScheduleService studyScheduleService;

    /**
     * 스터디 일정 CRUD
     */
    @GetMapping("/studyboard/{boardId}/study-schedule/add")
    public StudyScheduleDto studyScheduleForm(@RequestBody StudyScheduleDto scheduleDto) {
        return scheduleDto;
    }

    @Transactional
    @PostMapping("/studyboard/{boardId}/study-schedule/add")
    public StudyScheduleDto studyScheduleAdd(@Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult, @PathVariable Long boardId) {
        StudyPost findPost = studyPostService.findById(boardId).get();

//        StudySchedule studySchedule = studyScheduleService.save(scheduleDto);
        StudySchedule studySchedule = new StudySchedule(scheduleDto);
        findPost.addStudySchedule(studySchedule); //편의 메서드
        //쿼리 3번나감 개선방법 생각
        return scheduleDto;
    }

    @GetMapping("/studyboard/{boardId}/study-schedule/{id}/edit")
    public StudyScheduleDto studyScheduleUpdateForm(@PathVariable Long id) {
        StudySchedule findSchedule = studyScheduleService.findById(id).get();

        StudyScheduleDto studyScheduleDto = new StudyScheduleDto(findSchedule);

        return studyScheduleDto;
    }

    @Transactional
    @PatchMapping("/studyboard/{boardId}/study-schedule/{id}/edit")
    public StudyScheduleDto studyScheduleUpdate(
            @Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult,
            @PathVariable Long id, @PathVariable Long boardId) {

        StudySchedule findSchedule = studyScheduleService.findById(id).get();

        findSchedule.updateSchedule(scheduleDto.getScheduleName(), scheduleDto.getPeriod());

        return scheduleDto;
    }

    @Transactional
    @DeleteMapping("/studyboard/{boardId}/study-schedule/{id}/delete")
    public String studyScheduleDelete(@PathVariable Long boardId, @PathVariable Long id) {
        StudyPost findPost = studyPostService.findById(boardId).get();
        StudySchedule findSchedule = studyScheduleService.findById(id).get();

//        studyScheduleService.delete(findSchedule);
        findPost.removeStudySchedule(findSchedule);
        //쿼리 4번 개선방법 생각
        return "삭제되었습니다.";
    }

    @GetMapping("/studyboard/{boardId}/study-schedules")
    public Result studyScheduleList(@PathVariable Long boardId) {
        StudyPost studyPost = studyPostService.studyScheduleList(boardId);

        return new Result(new StudyPostScheduleSetDto(studyPost));
    }
}
