package backend.schedule.controller;


import backend.schedule.dto.StudyPostDto;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Slf4j
public class StudyPostController {

    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
    private final ApplicationMemberService applicationMemberService;
    private final StudyScheduleService studyScheduleService;

    @GetMapping("/studyboard/post")
    public StudyPostDto studyBoardForm(@RequestBody StudyPostDto postDto) {
        return postDto;
    }

    @PostMapping("/studyboard/post")
    public StudyPostDto studyBoardPost(@Valid @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult) {

//        if (bindingResult.hasErrors()) {
//            System.exit(0);
//        } //검증시 오류 제어해주는게 있어야 멈춤

        studyPostService.save(studyPostDto);
        log.info("data1={}, data2={}", studyPostDto.getPost(), studyPostDto.getStudyName());
        return studyPostDto;
    }
}
