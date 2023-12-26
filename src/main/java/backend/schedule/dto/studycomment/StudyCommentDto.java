package backend.schedule.dto.studycomment;

import backend.schedule.entity.StudyComment;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class StudyCommentDto {

    private Long commentId;

    private String nickname;

    @NotBlank(message = "댓글을 입력해 주세요")
    private String comment;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    private boolean myAuthority;

    public StudyCommentDto(StudyComment comment, Long memberId) {
        this.commentId = comment.getId();
        this.nickname = comment.getMember().getNickname();
        this.comment = comment.getComment();
        this.createdDate = comment.getCreatedDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        if (comment.getMember().getId() == memberId) {
            this.myAuthority = true;
        } else {
            this.myAuthority = false;
        }
    }
}
