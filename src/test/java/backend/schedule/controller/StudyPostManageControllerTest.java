package backend.schedule.controller;

import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StudyPostManageControllerTest {

    @Autowired
    StudyPostService studyPostService;

    @Autowired
    StudyScheduleService studyScheduleService;

}