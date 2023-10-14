package backend.schedule.dto;


import backend.schedule.enumlist.FieldTag;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Lob;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class StudyPostDto {

    //String만 NotEmpty지원
    @NotEmpty
    private String studyName;

    @NotNull
    private FieldTag tag;

    @NotNull
    private LocalDate period;

    @NotNull
    @Min(value = 1)
    @Max(value = 20)
    private int recruitMember;

    @NotNull
    private boolean onOff;

    @NotEmpty
    private String area;

    @Lob
    @NotEmpty
    private String post;

}
