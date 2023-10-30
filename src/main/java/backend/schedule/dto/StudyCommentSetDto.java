package backend.schedule.dto;

import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.enumlist.FieldTag;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StudyCommentSetDto {

    private String announcementTitle;

    private List<StudyCommentDto> studyComments;

    public StudyCommentSetDto(StudyAnnouncement announcement) {
        this.announcementTitle = announcement.getAnnouncementTitle();
        this.studyComments = announcement.getStudyComments().stream()
                .map(StudyCommentDto::new)
                .collect(Collectors.toList());
    }
}
