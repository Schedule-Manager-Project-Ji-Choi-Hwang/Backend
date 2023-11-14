package backend.schedule.controller;


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
//    @GetMapping("/studyboard/{boardId}/study-schedule/add")
//    public StudyScheduleDto studyScheduleForm(@RequestBody StudyScheduleDto scheduleDto) {
//        return scheduleDto;
//    }
    //스케쥴 단건조회

    @Transactional
    @PostMapping("/studyboard/{boardId}/study-schedule/add")
    public ResponseEntity<?> studyScheduleAdd(@Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult, @PathVariable Long boardId) {
        StudyPost findPost = studyPostService.findById(boardId);

        if (findPost == null) {
            return ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        }

//        StudySchedule studySchedule = studyScheduleService.save(scheduleDto);
        StudySchedule studySchedule = new StudySchedule(scheduleDto);
        findPost.addStudySchedule(studySchedule); //편의 메서드
        //쿼리 3번나감 개선방법 생각

        return ResponseEntity.ok().build();
    }

    @GetMapping({"/studyboard/{boardId}/study-schedule/{id}/edit", "/studyboard/{boardId}/study-schedule/{id}"}) //아무나 일정을 볼 수 있는 문제있음
    public ResponseEntity<?> studyScheduleUpdateForm(@PathVariable Long id) {
        StudySchedule findSchedule = studyScheduleService.findById(id);

        if (findSchedule == null) {
            return ResponseEntity.badRequest().body("일정을 찾을 수 없습니다.");
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
            return ResponseEntity.badRequest().body("일정을 찾을 수 없습니다.");
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
            return ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        } else if (findSchedule == null) {
            return ResponseEntity.badRequest().body("일정을 찾을 수 없습니다.");
        }

        findPost.removeStudySchedule(findSchedule);
        //쿼리 4번 개선방법 생각
        return ResponseEntity.ok().body("삭제되었습니다.");
    }

    @GetMapping("/studyboard/{boardId}/study-schedules")
    public ResponseEntity<Result> studyScheduleList(@PathVariable Long boardId) {
        StudyPost studyPost = studyPostService.studyScheduleList(boardId);//optional 쓸 수 있는지 해보기

        return ResponseEntity.ok().body(new Result(new StudyPostScheduleSetDto(studyPost)));
    }
}
