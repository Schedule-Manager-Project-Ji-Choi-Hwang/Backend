package backend.schedule.controller;

import backend.schedule.dto.ScheduleReqDto;
import backend.schedule.dto.ScheduleResDto;
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

    @PostMapping("/schedules/add") // 반복 등록 시 시작 및 종료 날짜 DB에도 넣기.
    public ResponseEntity<?> add(@RequestBody ScheduleReqDto scheduleReqDto) {
        String result = scheduleService.add(scheduleReqDto);
        if (result == null) {
            return ResponseEntity.badRequest().body("일정 등록 실패");
        }
        return ResponseEntity.ok().body("일정 등록 성공");
    }

    @GetMapping("/schedules")
    public ResponseEntity<?> memberBySchedules(HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);

        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();
        List<ScheduleResDto> schedules = scheduleService.findSchedulesByMemberId(memberId);
        return ResponseEntity.ok().body(schedules);
    }

    @PostMapping("/schedules/{id}/edit")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody ScheduleReqDto scheduleReqDto) {
        scheduleService.updateSchedule(id, scheduleReqDto);
        return ResponseEntity.ok("변경 되었습니다.");
    }

    @PostMapping("/schedules/{id}/delete")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok("삭제 되었습니다.");
    }
}
