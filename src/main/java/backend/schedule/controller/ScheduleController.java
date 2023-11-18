package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.ScheduleReqDto;
import backend.schedule.dto.ScheduleResDto;
import backend.schedule.entity.Schedule;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.MemberService;
import backend.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final MemberService memberService;
    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스케쥴 저장 기능
     * 요청 데이터 : 일정 제목, 날짜, 과목 이름(과목 id로 대체 예정)
     * (단일)요청 횟수 : 2회
     *          1. 개인 과목 조회
     *          2. 일정 추가
     *
     * 요청 데이터 : 일정 제목, 시작 날짜, 종료 날짜, 반복(repeat), 과목 이름(과목 id로 대체 예정)
     * (반복)요청 횟수 : 1 + N회
     *          1. 개인 과목 조회
     *          2. 일정 갯수 만큼 추가
     */
    @PostMapping("/schedules/add") // 반복 등록 시 시작 및 종료 날짜 DB에도 넣기.
    public ResponseEntity<?> add(@RequestBody ScheduleReqDto scheduleReqDto) {
        // 스케쥴 저장
        String result = scheduleService.add(scheduleReqDto);
        if (result == null) {
            return ResponseEntity.badRequest().body("일정 등록 실패");
        }
        
        // 응답
        return ResponseEntity.ok().body("일정 등록 성공");
    }

    /**
     * 스케쥴 전체 조회 기능 (멤버별)
     * 요청 데이터 : ''
     * 요청 횟수 : 2회
     *          1. 로그인 아이디 이용해 멤버 조회
     *          2. 개인과목-일정 fetch join
     */
    @GetMapping("/schedules")
    public ResponseEntity<?> memberBySchedules(HttpServletRequest request) {
        // 토큰 추출 및 멤버 식별
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();

        // 스케쥴 전체 조회 (멤버별)
        List<ScheduleResDto> schedules = scheduleService.findSchedulesByMemberId(memberId);

        // 응답
        return ResponseEntity.ok().body(schedules);
    }

    /**
     * 스케쥴 변경 기능 (제목, 기간(period))
     * 요청 데이터 : 일정 제목, 날짜(period)
     * 요청 횟수 : 2회
     *          1. 일정 id 이용해 일정 조회
     *          2. 일정 제목 변경
     */
    @PatchMapping("/schedules/{id}/edit")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody ScheduleReqDto scheduleReqDto) {
        // 스케쥴 변경
        Schedule schedule = scheduleService.updateSchedule(id, scheduleReqDto);
        if (schedule == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail("일정을 찾을 수 없습니다."));
        }

        // 응답
        return ResponseEntity.ok("변경 되었습니다.");
    }

    /**
     * 스케쥴 삭제 기능
     * 요청 데이터 : 일정 id(경로)
     * 요청 횟수 : 2회
     *          1. 일정 id 이용해 일정 조회
     *          2. 일정 삭제
     */
    @DeleteMapping("/schedules/{id}/delete")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        // 스케쥴 삭제
        scheduleService.deleteSchedule(id);

        // 응답
        return ResponseEntity.ok("삭제 되었습니다.");
    }
}
