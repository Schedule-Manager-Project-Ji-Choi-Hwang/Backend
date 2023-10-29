package backend.schedule.dto;


import backend.schedule.entity.StudyAnnouncement;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class StudyAnnouncementDto {

    @NotEmpty
    private String announcementTitle;

    @NotEmpty
    private String announcementPost;

    public StudyAnnouncementDto(StudyAnnouncement announcement) {
        this.announcementTitle = announcement.getAnnouncementTitle();
        this.announcementPost = announcement.getAnnouncementPost();
    }
}
