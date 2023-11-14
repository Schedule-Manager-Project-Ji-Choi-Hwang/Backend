package backend.schedule.dto;


import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.FieldTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Lob;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class StudyPostDto {

    //String만 NotEmpty지원
    @NotEmpty(message = "제목을 입력해 주세요.")
    private String studyName;

    private FieldTag tag;

    @NotNull(message = "일정을 입력해 주세요.")
    private LocalDate period;

    @NotNull(message = "모집 인원을 설정해 주세요.")
    @Min(value = 1, message = "1명 이상 설정 가능합니다.")
    @Max(value = 20, message = "20명 이하 설정 가능합니다.")
    private int recruitMember;

    @NotNull(message = "온라인/오프라인 설정")
    private boolean onOff;

    @NotEmpty(message = "지역을 선택해 주세요.")
    private String area;

//    @Lob
    @NotEmpty(message = "글을 작성해 주세요.")
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
