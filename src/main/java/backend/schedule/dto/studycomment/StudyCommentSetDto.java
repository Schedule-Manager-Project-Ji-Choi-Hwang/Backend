package backend.schedule.dto.studycomment;

import backend.schedule.entity.StudyAnnouncement;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StudyCommentSetDto {

    private List<StudyCommentDto> studyComments;

    public StudyCommentSetDto(StudyAnnouncement announcement) {
        this.studyComments = announcement.getStudyComments().stream()
                .map(StudyCommentDto::new)
                .collect(Collectors.toList());
    }
}
