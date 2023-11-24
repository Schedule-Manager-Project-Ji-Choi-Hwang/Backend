package backend.schedule.dto.applicationmember;

import backend.schedule.entity.ApplicationMember;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplicationMemberDto {

    private Long id;

    private String nickname;

    public ApplicationMemberDto(ApplicationMember applicationMember) {
        this.id = applicationMember.getId();
        this.nickname = applicationMember.getMember().getNickname();
    }
}
