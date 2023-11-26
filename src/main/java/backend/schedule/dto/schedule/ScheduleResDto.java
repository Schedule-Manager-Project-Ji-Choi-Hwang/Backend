package backend.schedule.dto.schedule;

import backend.schedule.entity.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResDto {

    private Long subjectId;

    private String subjectName;

    private List<ScheduleDto> schedules;

    public ScheduleResDto(Subject subject) {
        this.subjectId = subject.getId();
        this.subjectName = subject.getSubjectName();
        this.schedules = subject.getSchedules().stream()
                .map(ScheduleDto::new)
                .collect(Collectors.toList());
    }
}
