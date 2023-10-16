package backend.schedule.controller;


import backend.schedule.dto.StudyPostDto;
import backend.schedule.dto.StudyScheduleDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class StudyPostManageController {

    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
    private final ApplicationMemberService applicationMemberService;
    private final StudyScheduleService studyScheduleService;

    @GetMapping("/studyboard/post")
    public StudyPostDto studyBoardForm(@RequestBody StudyPostDto postDto) {
        return postDto;
    }

    @PostMapping("/studyboard/post") // 등록 버튼 누르면 post 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public StudyPostDto studyBoardPost(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult) {

//        if (bindingResult.hasErrors()) {
//            System.exit(0);
//        } //검증시 오류 제어해주는게 있어야 멈춤

        studyPostService.save(studyPostDto);
        return studyPostDto;
    }

    @GetMapping({"/studyboard/{id}", "/studyboard/{id}/edit"})
    public StudyPostDto studyBoardUpdateForm(@PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id).get(); //예외 처리가 필요한가 고민

        //중간에 이 게시글을 작성한 사람이 맞는지 확인하는 로직 필요

        StudyPostDto studyPostDto = StudyPostDto.builder()
                .studyName(findStudyPost.getStudyName())
                .tag(findStudyPost.getTag())
                .period(findStudyPost.getPeriod())
                .recruitMember(findStudyPost.getRecruitMember())
                .onOff(findStudyPost.isOnOff())
                .area(findStudyPost.getArea())
                .post(findStudyPost.getPost())
                .build();

        return studyPostDto;
    }

    @Transactional
    @PostMapping("/studyboard/{id}/edit") // 업데이트 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public StudyPostDto studyBoardUpdate(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult, @PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id).get();

        findStudyPost.updatePost(studyPostDto.getStudyName(), studyPostDto.getTag(), studyPostDto.getPeriod(),
                studyPostDto.getRecruitMember(), studyPostDto.isOnOff(), studyPostDto.getArea(), studyPostDto.getPost());

        return studyPostDto;
    }

    @DeleteMapping("/studyboard/{id}/delete") //삭제 성공하면 /studyboard 스터디 게시판으로 이동
    public String studyBoardDelete(@PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id).get();
        studyPostService.delete(findStudyPost);

        return "삭제되었습니다.";
    }

    @GetMapping("/studyboard/{boardId}/study-schedule/add")
    public StudyScheduleDto studyScheduleForm(@RequestBody StudyScheduleDto scheduleDto) {
        return scheduleDto;
    }

    @Transactional
    @PostMapping("/studyboard/{boardId}/study-schedule/add")
    public StudyScheduleDto studyScheduleAdd(@Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult, @PathVariable Long boardId) {
        StudyPost findPost = studyPostService.findById(boardId).get();

        StudySchedule studySchedule = studyScheduleService.save(scheduleDto);
        findPost.addStudySchedule(studySchedule); //편의 메서드

        return scheduleDto;
    }

    @GetMapping("/studyboard/{boardId}/study-schedule/{id}/edit")
    public StudyScheduleDto studyScheduleUpdateForm(@PathVariable Long id) {
        StudySchedule findSchedule = studyScheduleService.findById(id).get();

        StudyScheduleDto studyScheduleDto =
                new StudyScheduleDto(findSchedule.getScheduleName(), findSchedule.getPeriod());

        return studyScheduleDto;
    }

    @Transactional
    @PostMapping("/studyboard/{boardId}/study-schedule/{id}/edit")
    public StudyScheduleDto studyScheduleUpdate(@Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult, @PathVariable Long id, @PathVariable Long boardId) {
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
//        List<StudySchedule> studySchedules = findPost.getStudySchedules();
//        for (StudySchedule studySchedule : studySchedules) {
//            log.info("ddd={}", studySchedule.getScheduleName());
//        }
//        findPost.getStudySchedules().remove(findSchedule);
        //cascade, orphanremoval 고려
        //studypost에 있는 list studySchedules 삭제

        return "삭제되었습니다.";
    }

    @GetMapping("/study-schedules")
    public void studyScheduleFind() {
        studyScheduleService.findAll(); //리스트 반환방법 찾기
    }
}
