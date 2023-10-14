package backend.schedule.controller;


import backend.schedule.dto.StudyPostDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        log.info("data1={}, data2={}", studyPostDto.getPost(), studyPostDto.getStudyName());
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

    @PostMapping("/studyboard/{id}/edit") // 업데이트 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public StudyPostDto studyBoardUpdate(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult, @PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id).get();

        findStudyPost.updatePost(findStudyPost.getStudyName(), findStudyPost.getTag(),
                findStudyPost.getPeriod(), findStudyPost.getRecruitMember(),
                findStudyPost.isOnOff(), findStudyPost.getArea(), findStudyPost.getPost());

        return studyPostDto;
    }

    @PostMapping("/studyboard/{id}/delete") //삭제 성공하면 /studyboard 스터디 게시판으로 이동
    public String studyBoardDelete(@PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id).get();
        studyPostService.delete(findStudyPost);

        return "삭제되었습니다.";
    }
}
