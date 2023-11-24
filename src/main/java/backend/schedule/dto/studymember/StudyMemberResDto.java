package backend.schedule.dto.studymember;

import backend.schedule.entity.StudyMember;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyMemberResDto {

    private Long id;

    private String nickname;

    public StudyMemberResDto(StudyMember studyMember) {
        this.id = studyMember.getId();
        this.nickname = studyMember.getMember().getNickname();
    }
}
