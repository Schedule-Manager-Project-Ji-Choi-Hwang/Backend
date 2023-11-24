package backend.schedule.dto;

import backend.schedule.entity.StudyPost;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class StudyPostFrontSaveDto {

    private Long studyPostId;

    private String studyPostName;

    public StudyPostFrontSaveDto(StudyPost studyPost) {
        this.studyPostId = studyPost.getId();
        this.studyPostName = studyPost.getStudyName();
    }
}
