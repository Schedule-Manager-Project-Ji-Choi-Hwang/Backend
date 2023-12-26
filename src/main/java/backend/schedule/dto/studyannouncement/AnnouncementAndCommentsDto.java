package backend.schedule.dto.studyannouncement;

import backend.schedule.dto.studycomment.StudyCommentDto;
import backend.schedule.entity.StudyAnnouncement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class AnnouncementAndCommentsDto {

    private Long announcementId;

    private String announcementTitle;

    private String announcementPost;

    private LocalDateTime createDate;

    private List<StudyCommentDto> commentList;

    public AnnouncementAndCommentsDto(StudyAnnouncement studyAnnouncement, Long memberId) {
        this.announcementId = studyAnnouncement.getId();
        this.announcementTitle = studyAnnouncement.getAnnouncementTitle();
        this.announcementPost = studyAnnouncement.getAnnouncementPost();
        this.createDate = studyAnnouncement.getCreatedDate();
        this.commentList = studyAnnouncement.getStudyComments().stream()
                .map(comment -> new StudyCommentDto(comment, memberId))
                .collect(Collectors.toList());
    }
}
