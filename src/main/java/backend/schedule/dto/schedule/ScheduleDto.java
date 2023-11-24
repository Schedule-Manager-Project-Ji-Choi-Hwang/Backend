package backend.schedule.dto.schedule;

import backend.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ScheduleDto {
    private Long scheduleId;

    private String scheduleName;

    private LocalDate period;

    public ScheduleDto(Schedule schedule) {
        this.scheduleId = schedule.getId();
        this.scheduleName = schedule.getScheduleName();
        this.period = schedule.getPeriod();
    }
}
