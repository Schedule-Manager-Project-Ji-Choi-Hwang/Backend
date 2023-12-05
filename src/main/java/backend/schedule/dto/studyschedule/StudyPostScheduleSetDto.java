package backend.schedule.dto.studyschedule;

import backend.schedule.entity.StudyMember;
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

    private List<StudyScheduleResDto> studySchedules;

    public StudyPostScheduleSetDto(StudyMember studyMember) {
        this.studyPostId = studyMember.getStudyPost().getId();
        this.studyName = studyMember.getStudyPost().getStudyName();
        this.tag = studyMember.getStudyPost().getTag();
        this.studySchedules = studyMember.getStudyPost().getStudySchedules().stream()
                .map(StudyScheduleResDto::new)
                .collect(Collectors.toList());
    }
}
