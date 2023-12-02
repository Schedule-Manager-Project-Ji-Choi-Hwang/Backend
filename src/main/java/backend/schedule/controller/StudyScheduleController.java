package backend.schedule.controller;


import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.studyschedule.StudyPostScheduleSetDto;
import backend.schedule.dto.studyschedule.StudyScheduleDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ConfirmAuthor.*;
import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class StudyScheduleController {

    private final StudyPostService studyPostService;
    private final JwtTokenExtraction jwtTokenExtraction;
    private final StudyMemberService studyMemberService;
    private final StudyScheduleService studyScheduleService;
    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스터디 일정 추가
     * Query: 2번
     */
//    @Transactional
    @PostMapping("/studyboard/{studyBoardId}/study-schedule/add")
    public ResponseEntity<?> studyScheduleAdd(@Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult,
                                              @PathVariable Long studyBoardId, HttpServletRequest request) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);
            StudyPost findPost = studyPostService.findById(studyBoardId);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            studyScheduleService.save(scheduleDto, findPost);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 일정 조회
     * Query: 1번
     */
    @GetMapping("/studyboard/{studyBoardId}/study-schedule/{studyScheduleId}")
    public ResponseEntity<?> studyScheduleSearch(@PathVariable Long studyBoardId, @PathVariable Long studyScheduleId, HttpServletRequest request) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);

            StudySchedule findSchedule = studyScheduleService.findById(studyScheduleId);
            StudyScheduleDto studyScheduleDto = new StudyScheduleDto(findSchedule);

            return ResponseEntity.ok().body(studyScheduleDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 일정 수정 조회
     * Query: 1번
     */
    @GetMapping("/studyboard/{studyBoardId}/study-schedule/{studyScheduleId}/edit")
    public ResponseEntity<?> studyScheduleUpdateForm(@PathVariable Long studyBoardId, @PathVariable Long studyScheduleId, HttpServletRequest request) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);

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
    public ResponseEntity<?> studyScheduleList(@PathVariable Long studyBoardId, HttpServletRequest request) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);

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
//    @Transactional
    @PatchMapping("/studyboard/{studyBoardId}/study-schedule/{studyScheduleId}/edit")
    public ResponseEntity<?> studyScheduleUpdate(@Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult,
                                                 @PathVariable Long studyBoardId, @PathVariable Long studyScheduleId, HttpServletRequest request) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            studyScheduleService.studyScheduleUpdate(studyScheduleId, scheduleDto);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 일정 삭제
     * Query: 3번
     */
//    @Transactional
    @DeleteMapping("/studyboard/{studyBoardId}/study-schedule/{studyScheduleId}/delete")
    public ResponseEntity<?> studyScheduleDelete(@PathVariable Long studyBoardId, @PathVariable Long studyScheduleId, HttpServletRequest request) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);

            String removeStudySchedule = studyScheduleService.removeStudySchedule(studyBoardId, studyScheduleId);

//            StudyPost findPost = studyPostService.findById(studyBoardId);
//            StudySchedule findSchedule = studyScheduleService.findById(studyScheduleId);
//
//            findPost.removeStudySchedule(findSchedule);
            //쿼리 4번 개선방법 생각
            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(removeStudySchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}
