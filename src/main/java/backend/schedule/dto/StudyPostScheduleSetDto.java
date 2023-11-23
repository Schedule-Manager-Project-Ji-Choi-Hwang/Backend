package backend.schedule.dto;

import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.FieldTag;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StudyPostScheduleSetDto {

    private Long studyPostId;

    private String studyName;

    private FieldTag tag;

    private List<StudyScheduleDto> studySchedules;

    public StudyPostScheduleSetDto(StudyPost studyPost) {
        this.studyPostId = studyPost.getId();
        this.studyName = studyPost.getStudyName();
        this.tag = studyPost.getTag();
        this.studySchedules = studyPost.getStudySchedules().stream()
                .map(StudyScheduleDto::new)
                .collect(Collectors.toList());
    }
}
