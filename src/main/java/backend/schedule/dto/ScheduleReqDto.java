package backend.schedule.dto;

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

    @NotEmpty(message = "반복을 입력해주세요.")
    private String repeat;

    @NotNull(message = "시작일을 입력해주세요.")
    private LocalDate startDate;

    @NotNull(message = "종료일을 입력해주세요.")
    private LocalDate endDate;
}
