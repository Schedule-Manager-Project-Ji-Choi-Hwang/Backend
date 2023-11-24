package backend.schedule.dto.member;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class FindPasswordReqDto {

    @NotBlank(message = "로그인 아이디가 비어 있습니다.")
    private String loginId;
    @NotBlank(message = "이메일이 비어 있습니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}
