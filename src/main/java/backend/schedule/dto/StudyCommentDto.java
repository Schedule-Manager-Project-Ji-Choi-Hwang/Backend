package backend.schedule.dto;

import backend.schedule.entity.StudyComment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StudyCommentDto {

    private String comment;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    public StudyCommentDto(StudyComment comment) {
        this.comment = comment.getComment();
        this.createdDate = comment.getCreatedDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
    }
}
