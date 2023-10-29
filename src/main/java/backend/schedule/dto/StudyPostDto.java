package backend.schedule.dto;


import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.FieldTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Lob;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
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

//    @Lob
    @NotEmpty
    private String post;

    public StudyPostDto(StudyPost studyPost) {
        this.studyName = studyPost.getStudyName();
        this.tag = studyPost.getTag();
        this.period = studyPost.getPeriod();
        this.recruitMember = studyPost.getRecruitMember();
        this.onOff = studyPost.isOnOff();
        this.area = studyPost.getArea();
        this.post = studyPost.getPost();
    }
}
