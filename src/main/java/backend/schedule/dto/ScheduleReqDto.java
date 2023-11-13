package backend.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ScheduleReqDto {
    private String scheduleName;

    private LocalDate period;

    private String subjectName;

    private String repeat;

    private LocalDate startDate;

    private LocalDate endDate;
}
