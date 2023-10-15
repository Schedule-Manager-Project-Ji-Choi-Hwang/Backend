package backend.schedule.controller;

import backend.schedule.dto.StudyPostDto;
import backend.schedule.dto.StudyScheduleDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.enumlist.FieldTag;
import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Transactional
class StudyPostManageControllerTest {

    @Autowired
    StudyPostService studyPostService;

    @Autowired
    StudyScheduleService studyScheduleService;

    @Test
    @Rollback(value = false)
    void studyScheduleTest() {
        StudyPostDto build = StudyPostDto.builder()
                .studyName("spring")
                .tag(FieldTag.DEVELOP)
                .period(LocalDate.now())
                .recruitMember(10)
                .onOff(true)
                .area("seoul")
                .post("hi!")
                .build();

        studyPostService.save(build);

        StudyScheduleDto studyScheduleDto = new StudyScheduleDto("스프링", LocalDate.now());
        StudySchedule save = studyScheduleService.save(studyScheduleDto);
        StudyPost studyPost = studyPostService.findById(1L).get();
        studyPost.addStudySchedule(save);

        List<StudySchedule> studySchedules = studyPost.getStudySchedules();
        for (StudySchedule studySchedule : studySchedules) {
            System.out.println(studySchedule.getScheduleName());
        }

        StudySchedule studySchedule = studyScheduleService.findById(1L).get();
        studySchedule.updateSchedule("Java", LocalDate.now());

        studyScheduleService.delete(studySchedule);
        studyPost.removeStudySchedule(studySchedule);

        List<StudySchedule> studySchedules1 = studyPost.getStudySchedules();
        for (StudySchedule schedule : studySchedules1) {
            System.out.println("data= "+schedule.getScheduleName());
        }

    }
}