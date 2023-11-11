package backend.schedule.dto;


import backend.schedule.entity.StudySchedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StudyScheduleDto {

    private String scheduleName;

    private LocalDate period;

    public StudyScheduleDto(StudySchedule studySchedule) {
        this.scheduleName = studySchedule.getScheduleName();
        this.period = studySchedule.getPeriod();
    }
}
