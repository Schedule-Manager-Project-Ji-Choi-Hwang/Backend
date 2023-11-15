package backend.schedule.dto;


import backend.schedule.entity.StudyAnnouncement;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class StudyAnnouncementDto {

    @NotBlank(message = "제목을 입력해 주세요.")
    private String announcementTitle;

    @NotBlank(message = "글을 작성해 주세요.")
    private String announcementPost;

    public StudyAnnouncementDto(StudyAnnouncement announcement) {
        this.announcementTitle = announcement.getAnnouncementTitle();
        this.announcementPost = announcement.getAnnouncementPost();
    }
}
