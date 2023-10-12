package backend.schedule.dto;

import backend.schedule.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MemberJoinDto {
    @NotBlank(message = "로그인 아이디가 비어있습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "닉네임이 비어있습니다.")
    private String nickname;

    @NotBlank(message = "이메일이 비어있습니다.")
    private String email;

    /**
     * 비밀번호 암호화
     */
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .loginId(this.loginId)
                .password(encodedPassword)
                .nickname(this.nickname)
                .email(this.email)
                .build();
    }
}
