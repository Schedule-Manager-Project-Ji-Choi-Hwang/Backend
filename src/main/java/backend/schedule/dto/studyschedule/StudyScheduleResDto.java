package backend.schedule.dto.studyschedule;

import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StudyScheduleResDto {

    private Long studyScheduleId;

    private String StudyScheduleName;

    private LocalDate period;

    public StudyScheduleResDto(StudySchedule studySchedule) {
        this.studyScheduleId = studySchedule.getId();
        StudyScheduleName = studySchedule.getScheduleName();
        this.period = studySchedule.getPeriod();
    }
}
