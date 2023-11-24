package backend.schedule.dto.subject;

import backend.schedule.entity.PersonalSubject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PersonalSubjectResDto {

    private Long subjectId;

    private String subjectName;


    public PersonalSubjectResDto(PersonalSubject personalSubject) {
        this.subjectId = personalSubject.getId();
        this.subjectName = personalSubject.getSubjectName();
    }
}
