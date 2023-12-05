package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.mainpage.ReturnLocalDateDto;
import backend.schedule.dto.schedule.ScheduleResDto;
import backend.schedule.dto.studyschedule.StudyPostScheduleSetDto;
import backend.schedule.entity.Member;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    private final MemberService memberService;

    private final StudyScheduleService studyScheduleService;
    private final JwtTokenExtraction jwtTokenExtraction;

    private final StudyMemberService studyMemberService;

    private final StudyPostService studyPostService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    @GetMapping("/main")
    public ResponseEntity<?> test(HttpServletRequest request,@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        // 토큰 추출 및 멤버 식별
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            List<Object> return_data = new ArrayList();

            List<ScheduleResDto> scheduleResDtos = scheduleService.findSchedulesByMemberId(memberId, date);
            return_data.addAll(scheduleResDtos);

//            List<Long> studyPostIds = studyMemberService.findStudyPostIds(findMember.getId());
//            for (Long studyPostId : studyPostIds) {
//                return_data.add(studyPostService.detailStudySchedules(studyPostId, date));
//            }
            List<StudyPostScheduleSetDto> studySchedules = studyScheduleService.findSchedules(memberId, date);
            return_data.addAll(studySchedules);


            return ResponseEntity.ok().body(new Result(return_data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}