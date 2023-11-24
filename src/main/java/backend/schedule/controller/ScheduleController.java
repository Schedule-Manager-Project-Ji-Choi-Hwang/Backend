package backend.schedule.controller;

import backend.schedule.dto.*;
import backend.schedule.entity.PersonalSubject;
import backend.schedule.entity.Schedule;
import backend.schedule.service.MemberService;
import backend.schedule.service.PersonalSubjectService;
import backend.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final MemberService memberService;
    private final PersonalSubjectService personalSubjectService;
    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스케쥴 저장 기능
     * 요청 데이터 : 일정 제목, 날짜, 과목 이름(과목 id로 대체 예정)
     * (단일)요청 횟수 : 2회
     * 1. 개인 과목 조회
     * 2. 일정 추가
     * <p>
     * 요청 데이터 : 일정 제목, 시작 날짜, 종료 날짜, 반복(repeat), 과목 이름(과목 id로 대체 예정)
     * (반복)요청 횟수 : 1 + N회
     * 1. 개인 과목 조회
     * 2. 일정 갯수 만큼 추가
     */
    @PostMapping("/subjects/{subjectId}/schedules/add") // 반복 등록 시 시작 및 종료 날짜 DB에도 넣기.
    public ResponseEntity<?> add(@PathVariable Long subjectId, @Validated @RequestBody ScheduleReqDto scheduleReqDto, BindingResult bindingResult) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        // 스케쥴 저장
        String result = scheduleService.add(scheduleReqDto, subjectId);
        if (result == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SCHEDULEFAIL));
        }

        // 응답
        return ResponseEntity.ok().body("일정 등록 성공");
    }

    /**
     * 스케쥴 단일 조회 기능
     * 요청 데이터 : ''
     * 요청 횟수 : 1회
     * 1. 일정 조회
     */
    @GetMapping("/subjects/schedules/{scheduleId}")
    public ResponseEntity<?> findSchedule(@PathVariable Long scheduleId) {
        Schedule findSchedule = scheduleService.findOne(scheduleId);
        if (findSchedule == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SCHEDULE));
        }

        ScheduleDto scheduleDto = new ScheduleDto(findSchedule);

        return ResponseEntity.ok().body(new Result(scheduleDto));
    }

    /**
     * 스케쥴 전체 조회 기능 (멤버별)
     * 요청 데이터 : ''
     * 요청 횟수 : 2회
     *          1. 로그인 아이디 이용해 멤버 조회
     *          2. 개인과목-일정 fetch join
     */
//    @GetMapping("/subjects/schedules")
//    public ResponseEntity<?> memberBySchedules(HttpServletRequest request) {
//        // 토큰 추출 및 멤버 식별
//        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
//        String secretKey = mySecretkey;
//        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
//        Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();
//
//        // 스케쥴 전체 조회 (멤버별)
//        List<ScheduleResDto> schedules = scheduleService.findSchedulesByMemberId(memberId);
//
//        // 응답
//        return ResponseEntity.ok().body(schedules);
//    }

    /**
     * 스케쥴 변경 기능 (제목, 기간(period))
     * 요청 데이터 : 일정 제목, 날짜(period)
     * 요청 횟수 : 2회
     * 1. 일정 id 이용해 일정 조회
     * 2. 일정 제목 변경
     */
    @PatchMapping("/subjects/schedules/{scheduleId}/edit")
    public ResponseEntity<?> updateSchedule(@PathVariable Long scheduleId, @Validated @RequestBody ScheduleEditReqDto scheduleEditReqDto, BindingResult bindingResult) {
        // 빈 검증
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        // 스케쥴 변경
        Schedule schedule = scheduleService.updateSchedule(scheduleId, scheduleEditReqDto);
        if (schedule == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SCHEDULE));
        }

        // 응답
        return ResponseEntity.ok("변경 되었습니다.");
    }

    /**
     * 스케쥴 삭제 기능
     * 요청 데이터 : 일정 id(경로)
     * 요청 횟수 : 4회
     * 1. 개인 과목 조회
     * 2. 일정 조회
     * 3. 개인 과목 id로 일정 조회
     * 4. 일정 삭제
     */
    @DeleteMapping("/subjects/{subjectId}/schedules/{scheduleId}/delete")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long subjectId, @PathVariable Long scheduleId) {
        PersonalSubject findSubject = personalSubjectService.findOne(subjectId);
        Schedule findSchedule = scheduleService.findOne(scheduleId);

        if (findSubject == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SUBJECT));
        } else if (findSchedule == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(SCHEDULE));
        }

        // 스케쥴 삭제
        scheduleService.deleteSchedule(findSubject, findSchedule);

        // 응답
        return ResponseEntity.ok("삭제 되었습니다.");
    }
}
