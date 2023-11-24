package backend.schedule.dto.mainpage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Setter
public class ReturnLocalDateDto {

    private LocalDate date;
}
