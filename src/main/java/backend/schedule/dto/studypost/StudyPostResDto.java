package backend.schedule.dto.studypost;

import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.FieldTag;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class StudyPostResDto {

    private Long id;

    private String studyName;

    private FieldTag tag;

    private int recruitMember;

    private boolean onOff;

    private String area;

    private String post;

    private boolean authority;

    @QueryProjection
    public StudyPostResDto(Long id, String studyName, FieldTag tag, int recruitMember, boolean onOff, String area, String post) {
        this.id = id;
        this.studyName = studyName;
        this.tag = tag;
        this.recruitMember = recruitMember;
        this.onOff = onOff;
        this.area = area;
        this.post = post;
    }

    public StudyPostResDto(StudyPost studyPost) {
        this.id = studyPost.getId();
        this.studyName = studyPost.getStudyName();
        this.tag = studyPost.getTag();
        this.recruitMember = studyPost.getRecruitMember();
        this.onOff = studyPost.isOnOff();
        this.area = studyPost.getArea();
        this.post = studyPost.getPost();
    }

    public StudyPostResDto(StudyPost studyPost, boolean authority) {
        this.id = studyPost.getId();
        this.studyName = studyPost.getStudyName();
        this.tag = studyPost.getTag();
        this.recruitMember = studyPost.getRecruitMember();
        this.onOff = studyPost.isOnOff();
        this.area = studyPost.getArea();
        this.post = studyPost.getPost();
        this.authority = authority;
    }


}
