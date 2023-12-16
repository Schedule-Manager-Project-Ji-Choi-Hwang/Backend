package backend.schedule.dto.studyannouncement;

import backend.schedule.entity.StudyAnnouncement;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class StudyAnnouncementEditDto {

    @NotBlank(message = "글을 작성해 주세요.")
    private String announcementPost;

    public StudyAnnouncementEditDto(StudyAnnouncement announcement) {
        this.announcementPost = announcement.getAnnouncementPost();
    }
}
