package backend.schedule.dto;

import backend.schedule.enumlist.FieldTag;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class StudyPostResponseDto {

    private Long id;

    private String studyName;

    private FieldTag tag;

    private LocalDate period;

    private int recruitMember;

    private boolean onOff;

    private String area;

    private String post;

    @QueryProjection
    public StudyPostResponseDto(Long id, String studyName, FieldTag tag, LocalDate period, int recruitMember, boolean onOff, String area, String post) {
        this.id = id;
        this.studyName = studyName;
        this.tag = tag;
        this.period = period;
        this.recruitMember = recruitMember;
        this.onOff = onOff;
        this.area = area;
        this.post = post;
    }
}
