package backend.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResDto {

    private String personalSubjectName;

    private String scheduleName;

    private LocalDate period;
}
