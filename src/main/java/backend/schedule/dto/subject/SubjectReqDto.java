package backend.schedule.dto.subject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class SubjectReqDto {

    @NotBlank(message = "과목 이름이 비어 있습니다.")
    private String subjectName;

    @NotBlank(message = "과목 색상이 비어 있습니다.")
    private String color;
}
