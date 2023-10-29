package backend.schedule.dto;

import backend.schedule.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class PersonalSubjectDto {

    private Member member;
    @NotBlank(message = "과목 이름이 비어 있습니다.")
    private String subjectName;
}
