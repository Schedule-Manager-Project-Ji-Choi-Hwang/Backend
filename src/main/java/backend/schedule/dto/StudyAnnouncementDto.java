package backend.schedule.dto;


import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class StudyAnnouncementDto {

    @NotEmpty
    private String announcementTitle;

    @NotEmpty
    private String announcementPost;
}
