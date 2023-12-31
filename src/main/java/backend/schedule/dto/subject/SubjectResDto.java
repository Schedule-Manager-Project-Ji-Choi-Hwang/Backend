package backend.schedule.dto.subject;

import backend.schedule.entity.Subject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubjectResDto {

    private Long subjectId;

    private String subjectName;

    private String color;


    public SubjectResDto(Subject subject) {
        this.subjectId = subject.getId();
        this.subjectName = subject.getSubjectName();
        this.color = subject.getColor();
    }
}
