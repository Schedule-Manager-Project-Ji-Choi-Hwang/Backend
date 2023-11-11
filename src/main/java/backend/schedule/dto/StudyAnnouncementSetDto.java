package backend.schedule.dto;


import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.FieldTag;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StudyAnnouncementSetDto {

    private String studyName;

    private FieldTag tag;

    private List<StudyAnnouncementDto> studyAnnouncements;

    public StudyAnnouncementSetDto(StudyPost studyPost) {
        this.studyName = studyPost.getStudyName();
        this.tag = studyPost.getTag();
        this.studyAnnouncements = studyPost.getStudyAnnouncements().stream()
                .map(StudyAnnouncementDto::new)
                .collect(Collectors.toList());
    }
}
