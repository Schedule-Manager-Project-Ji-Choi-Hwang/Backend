package backend.schedule.dto.studyschedule;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StudyScheduleReqDto {

    @NotBlank(message = "일정 이름을 입력해 주세요.")
    private String studyScheduleName;

    private String repeat;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate period;

}
