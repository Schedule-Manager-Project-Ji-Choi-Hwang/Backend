package backend.schedule.entity;

import backend.schedule.dto.studyschedule.StudyScheduleEditReqDto;
import backend.schedule.dto.studyschedule.StudyScheduleReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
public class StudySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyschedule_id", updatable = false)
    private Long id;

    private String scheduleName;

    private LocalDate period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studypost_id")
    private StudyPost studyPost;

    public StudySchedule(StudyScheduleReqDto scheduleReqDto, LocalDate date) {
        this.scheduleName = scheduleReqDto.getStudyScheduleName();
        this.period = date;
    }

    public void updateSchedule(StudyScheduleEditReqDto scheduleEditReqDto) {
        this.scheduleName = scheduleEditReqDto.getScheduleName();
        this.period = scheduleEditReqDto.getPeriod();
    }

    public StudySchedule testSchedule(String scheduleName, LocalDate period) {
        this.scheduleName = scheduleName;
        this.period = period;

        return this;
    }

    public void setStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
    }
}
