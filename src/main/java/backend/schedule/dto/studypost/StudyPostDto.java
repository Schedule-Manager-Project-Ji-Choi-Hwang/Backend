package backend.schedule.dto.studypost;


import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.FieldTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Lob;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StudyPostDto {

    @NotBlank(message = "제목을 입력해 주세요.")
    private String studyName;

    private FieldTag tag;

    @NotNull(message = "일정을 입력해 주세요.")
    private LocalDate period;

    @NotNull(message = "모집 인원을 설정해 주세요.")
    @Range(min = 1, max = 20, message = "1명 이상 20명 이하만 모집 가능합니다.")
    private int recruitMember;

    @NotNull(message = "온라인/오프라인 설정")
    private boolean onOff;

    @NotBlank(message = "지역을 선택해 주세요.")
    private String area;

    @NotBlank(message = "글을 작성해 주세요.")
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
