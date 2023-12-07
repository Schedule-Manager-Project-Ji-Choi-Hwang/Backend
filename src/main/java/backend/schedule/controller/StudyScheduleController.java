package backend.schedule.controller;


import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.studyschedule.StudyScheduleEditReqDto;
import backend.schedule.dto.studyschedule.StudyScheduleReqDto;
import backend.schedule.dto.studyschedule.StudyScheduleResDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ConfirmAuthor.LEADER;
import static backend.schedule.validation.RequestDataValidation.beanValidation;

@RestController
@RequiredArgsConstructor
public class StudyScheduleController {

    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
    private final JwtTokenExtraction jwtTokenExtraction;
    private final StudyScheduleService studyScheduleService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스터디 일정 추가
     * Query: 2번
     */
    @PostMapping("/study-board/{studyBoardId}/study-schedule/add")
    public ResponseEntity<?> studyScheduleAdd(@Validated @RequestBody StudyScheduleReqDto scheduleReqDto, BindingResult bindingResult,
                                              @PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);
            StudyPost findPost = studyPostService.findById(studyBoardId);

            if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            studyScheduleService.addStudySchedule(scheduleReqDto, findPost);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 일정 조회
     * Query: 1번
     */
    @GetMapping("/study-board/{studyBoardId}/study-schedule/{studyScheduleId}")
    public ResponseEntity<?> studyScheduleSearch(@PathVariable Long studyBoardId, @PathVariable Long studyScheduleId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);

            StudySchedule findStudySchedule = studyScheduleService.findSchedule(studyBoardId, studyScheduleId);

            return ResponseEntity.ok().body(new StudyScheduleResDto(findStudySchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스터디 일정 수정 조회
     * Query: 1번
     */
    @GetMapping("/study-board/{studyBoardId}/study-schedule/{studyScheduleId}/edit")
    public ResponseEntity<?> studyScheduleUpdateForm(@PathVariable Long studyBoardId, @PathVariable Long studyScheduleId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);

            StudySchedule findStudySchedule = studyScheduleService.findSchedule(studyBoardId, studyScheduleId);

            return ResponseEntity.ok().body(new StudyScheduleResDto(findStudySchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

//    /**
//     * 스터디 일정 전체조회
//     * Query: Fetch join이용 1번
//     */
//    @GetMapping("/studyboard/{studyBoardId}/study-schedules")
//    public ResponseEntity<?> studyScheduleList(@PathVariable Long studyBoardId, HttpServletRequest request) {
//        //다시 확인
//        try {
//            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
//            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);
//
//            StudyPost studyPost = studyPostService.studyScheduleList(studyBoardId);
//
//            return ResponseEntity.ok().body(new Result(new StudyPostScheduleSetDto(studyPost)));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
//        }
//
//    }

    /**
     * 스터디 일정 수정
     * Query: 2번
     */
//    @Transactional
    @PatchMapping("/study-board/{studyBoardId}/study-schedule/{studyScheduleId}/edit")
    public ResponseEntity<?> studyScheduleUpdate(@Validated @RequestBody StudyScheduleEditReqDto scheduleEditReqDto, BindingResult bindingResult,
                                                 @PathVariable Long studyBoardId, @PathVariable Long studyScheduleId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);

            StudySchedule findStudySchedule = studyScheduleService.findSchedule(studyBoardId, studyScheduleId);

            if (bindingResult.hasErrors()) return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            studyScheduleService.updateStudySchedule(findStudySchedule, scheduleEditReqDto);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 일정 삭제
     * Query: 3번
     */
    @DeleteMapping("/study-board/{studyBoardId}/study-schedule/{studyScheduleId}/delete")
    public ResponseEntity<?> studyScheduleDelete(@PathVariable Long studyBoardId, @PathVariable Long studyScheduleId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);

            String removeStudySchedule = studyScheduleService.removeStudySchedule(studyBoardId, studyScheduleId);

            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(removeStudySchedule));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }
}
