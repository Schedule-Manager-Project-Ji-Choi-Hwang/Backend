package backend.schedule.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MemberLoginReqDto {

    @NotBlank(message = "로그인 아이디가 비어있습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
}
