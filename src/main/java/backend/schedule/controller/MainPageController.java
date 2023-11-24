package backend.schedule.controller;

import backend.schedule.dto.Result;
import backend.schedule.dto.mainpage.ReturnLocalDateDto;
import backend.schedule.dto.schedule.ScheduleResDto;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.MemberService;
import backend.schedule.service.ScheduleService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MainPageController {

    private final ScheduleService scheduleService;

    private final MemberService memberService;

    private final StudyMemberService studyMemberService;

    private final StudyPostService studyPostService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    @GetMapping("/main")
    public ResponseEntity<?> test(HttpServletRequest request, @RequestBody ReturnLocalDateDto returnLocalDateDto) {
        // 토큰 추출 및 멤버 식별
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        Long memberId = memberService.getLoginMemberByLoginId(memberLoginId).getId();

        List<Object> return_data = new ArrayList();

        List<ScheduleResDto> scheduleResDtos = scheduleService.findSchedulesByMemberId(memberId, returnLocalDateDto.getDate());
        return_data.addAll(scheduleResDtos);

        List<Long> studyPostIds = studyMemberService.findStudyPostIds(memberId);
        for (Long studyPostId : studyPostIds) {
            return_data.add(studyPostService.detailStudySchedules(studyPostId, returnLocalDateDto.getDate()));
        }

        return ResponseEntity.ok().body(new Result(return_data));
    }
}