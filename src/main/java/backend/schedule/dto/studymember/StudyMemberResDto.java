package backend.schedule.dto.studymember;

import backend.schedule.entity.StudyMember;
import backend.schedule.enumlist.ConfirmAuthor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyMemberResDto {

    private Long studyMemberId;

    private String nickname;

    private ConfirmAuthor authority;

    public StudyMemberResDto(StudyMember studyMember) {
        this.studyMemberId = studyMember.getId();
        this.nickname = studyMember.getMember().getNickname();
        this.authority = studyMember.getConfirmAuthor();
    }
}
