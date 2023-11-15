package backend.schedule.dto;


import backend.schedule.entity.StudySchedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StudyScheduleDto {

    @NotBlank(message = "일정 이름을 입력해 주세요.")
    private String scheduleName;

    @NotNull(message = "일정을 입력해 주세요.")
    private LocalDate period;

    public StudyScheduleDto(StudySchedule studySchedule) {
        this.scheduleName = studySchedule.getScheduleName();
        this.period = studySchedule.getPeriod();
    }
}
