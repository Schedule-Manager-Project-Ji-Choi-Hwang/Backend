package backend.schedule.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleEditReqDto {

    @NotEmpty(message = "일정 제목을 입력해주세요.")
    private String scheduleName;

    @NotNull(message = "날짜를 입력해주세요.")
    private LocalDate period;
}
