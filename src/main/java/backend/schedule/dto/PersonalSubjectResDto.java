package backend.schedule.dto;

import backend.schedule.entity.PersonalSubject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PersonalSubjectResDto {

    private String subjectName;


    public PersonalSubjectResDto(PersonalSubject personalSubject) {
        this.subjectName = personalSubject.getSubjectName();
    }
}
