package backend.schedule.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindLoginIdResDto {

    private String loginId;

    private String message;
}
