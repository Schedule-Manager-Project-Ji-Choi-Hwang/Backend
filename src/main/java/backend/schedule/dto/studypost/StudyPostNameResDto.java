package backend.schedule.dto.studypost;

import backend.schedule.entity.StudyMember;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyPostNameResDto {

    private Long studyPostId;

    private String studyPostName;

    public StudyPostNameResDto(StudyMember studyMember) {
        this.studyPostId = studyMember.getStudyPost().getId();
        this.studyPostName = studyMember.getStudyPost().getStudyName();
    }
}
