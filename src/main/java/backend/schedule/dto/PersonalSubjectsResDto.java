package backend.schedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PersonalSubjectsResDto {

    private final List<PersonalSubjectResDto> personalSubjectResDtoList = new ArrayList<>();
}
