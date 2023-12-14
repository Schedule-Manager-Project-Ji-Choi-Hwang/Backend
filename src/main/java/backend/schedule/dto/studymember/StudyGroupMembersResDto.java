package backend.schedule.dto.studymember;

import backend.schedule.entity.StudyMember;
import backend.schedule.enumlist.ConfirmAuthor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static backend.schedule.enumlist.ConfirmAuthor.*;

@Getter
@NoArgsConstructor
public class StudyGroupMembersResDto {

    private Long studyMemberId;

    private String nickname;

    private ConfirmAuthor authority;

    private boolean leader;

    public StudyGroupMembersResDto(StudyMember studyMember) {
        this.studyMemberId = studyMember.getId();
        this.nickname = studyMember.getMember().getNickname();
        this.authority = studyMember.getConfirmAuthor();

        if (studyMember.getConfirmAuthor() == LEADER) leader = true;
        else leader = false;
    }
}
