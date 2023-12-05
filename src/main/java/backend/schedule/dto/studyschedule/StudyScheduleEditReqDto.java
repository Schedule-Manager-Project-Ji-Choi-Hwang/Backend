package backend.schedule.dto.studyschedule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StudyScheduleEditReqDto {

    @NotEmpty(message = "일정 제목을 입력해주세요.")
    private String scheduleName;

    @NotNull(message = "날짜를 선택해 주세요.")
    private LocalDate period;
}
