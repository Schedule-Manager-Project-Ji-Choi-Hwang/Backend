package backend.schedule.entity;

import backend.schedule.dto.schedule.ScheduleEditReqDto;
import backend.schedule.dto.schedule.ScheduleReqDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(name = "SCHEDULE_SEQ_GENERATOR",
        sequenceName = "SCHEDULE_SEQ")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SCHEDULE_SEQ_GENERATOR")
    @Column(name = "schedule_id", updatable = false)
    private Long id;

    private String scheduleName;

    private LocalDate period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ps_id")
    private Subject subject;

    public Schedule(ScheduleReqDto scheduleReqDto, LocalDate date) {
        this.scheduleName = scheduleReqDto.getScheduleName();
        this.period = date;
    }

    public Schedule(String scheduleName, LocalDate period, Subject subject) {
        this.scheduleName = scheduleName;
        this.period = period;
        this.subject = subject;
    }

    public void changeSchedule(ScheduleEditReqDto scheduleEditReqDto) {
        this.scheduleName = scheduleEditReqDto.getScheduleName();
        this.period = scheduleEditReqDto.getPeriod();
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
