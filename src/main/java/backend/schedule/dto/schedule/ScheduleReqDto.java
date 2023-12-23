package backend.schedule.dto.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ScheduleReqDto {
    @NotEmpty(message = "일정 제목을 입력해주세요.")
    private String scheduleName;

    private String repeat;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate period;
}
