package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.schedule.ScheduleResDto;
import backend.schedule.dto.studyschedule.StudyPostScheduleSetDto;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.ScheduleService;
import backend.schedule.service.StudyScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainPageController {

    private final ScheduleService scheduleService;
    private final StudyScheduleService studyScheduleService;
    private final JwtTokenExtraction jwtTokenExtraction;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 메인 페이지
     * @param date 조회할 날짜
     */
    @GetMapping("/main")
    public ResponseEntity<?> test(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            List<Object> return_data = new ArrayList();
            List<ScheduleResDto> scheduleResDtos = scheduleService.findSchedulesByMemberId(memberId, date);
            return_data.addAll(scheduleResDtos);

            List<StudyPostScheduleSetDto> studySchedules = studyScheduleService.findSchedules(memberId, date);
            return_data.addAll(studySchedules);

            return ResponseEntity.ok().body(new Result(return_data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}