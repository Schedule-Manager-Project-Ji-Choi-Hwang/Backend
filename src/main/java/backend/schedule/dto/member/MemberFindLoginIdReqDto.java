package backend.schedule.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MemberFindLoginIdReqDto {

    @NotBlank(message = "이메일이 비어 있습니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}
