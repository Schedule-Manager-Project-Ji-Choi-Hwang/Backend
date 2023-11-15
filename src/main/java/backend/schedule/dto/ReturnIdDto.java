package backend.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnIdDto {

    private Long id1;

    private Long id2;

    public ReturnIdDto(Long id1) {
        this.id1 = id1;
    }

    public ReturnIdDto(Long id1, Long id2) {
        this.id1 = id1;
        this.id2 = id2;
    }
}
