package backend.schedule.dto;

import backend.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ScheduleDto {
    private String scheduleName;

    private LocalDate period;

    public ScheduleDto(Schedule schedule) {
        this.scheduleName = schedule.getScheduleName();
        this.period = schedule.getPeriod();
    }
}
