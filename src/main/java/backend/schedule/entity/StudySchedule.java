package backend.schedule.entity;

import backend.schedule.dto.StudyScheduleDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(name = "STUDYSCHEDULE_SEQ_GENERATOR",
        sequenceName = "STUDYSCHEDULE_SEQ")
public class StudySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STUDYSCHEDULE_SEQ_GENERATOR")
    @Column(name = "studyschedule_id", updatable = false)
    private Long id;

    private String scheduleName;

    private LocalDate period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studypost_id")
    private StudyPost studyPost;

    public StudySchedule(StudyScheduleDto scheduleDto) {
        this.scheduleName = scheduleDto.getScheduleName();
        this.period = scheduleDto.getPeriod();
    }

    public void updateSchedule(String scheduleName, LocalDate period) {
        this.scheduleName = scheduleName;
        this.period = period;
    }



    public void setStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
    }
}
