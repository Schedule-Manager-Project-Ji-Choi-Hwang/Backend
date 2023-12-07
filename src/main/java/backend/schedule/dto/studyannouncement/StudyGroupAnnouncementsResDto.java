package backend.schedule.dto.studyannouncement;

import backend.schedule.entity.StudyAnnouncement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StudyGroupAnnouncementsResDto {

    private Long announcementId;

    private String announcementTitle;

    private LocalDateTime createDate;

    public StudyGroupAnnouncementsResDto(StudyAnnouncement studyAnnouncement) {
        this.announcementId = studyAnnouncement.getId();
        this.announcementTitle = studyAnnouncement.getAnnouncementTitle();
        this.createDate = studyAnnouncement.getCreatedDate();
    }
}
