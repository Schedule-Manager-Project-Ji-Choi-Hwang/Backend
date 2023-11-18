package backend.schedule.dto;

import backend.schedule.entity.PersonalSubject;
import backend.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResDto {

    private String personalSubjectName;

    private List<ScheduleDto> schedules;

    public ScheduleResDto(PersonalSubject personalSubject) {
        this.personalSubjectName = personalSubject.getSubjectName();
        this.schedules = personalSubject.getSchedules().stream()
                .map(ScheduleDto::new)
                .collect(Collectors.toList());
    }
}
