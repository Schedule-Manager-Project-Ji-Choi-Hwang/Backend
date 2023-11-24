package backend.schedule.dto.schedule;

import backend.schedule.entity.PersonalSubject;
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

    public ScheduleResDto(PersonalSubject personalSubject) {
        this.subjectId = personalSubject.getId();
        this.subjectName = personalSubject.getSubjectName();
        this.schedules = personalSubject.getSchedules().stream()
                .map(ScheduleDto::new)
                .collect(Collectors.toList());
    }
}
