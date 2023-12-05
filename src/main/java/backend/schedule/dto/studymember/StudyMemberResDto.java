package backend.schedule.dto.studymember;

import backend.schedule.entity.StudyMember;
import backend.schedule.enumlist.ConfirmAuthor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyMemberResDto {

    private Long id;

    private String nickname;

    private ConfirmAuthor authority;

    public StudyMemberResDto(StudyMember studyMember) {
        this.id = studyMember.getId();
        this.nickname = studyMember.getMember().getNickname();
        this.authority = studyMember.getConfirmAuthor();
    }
}
