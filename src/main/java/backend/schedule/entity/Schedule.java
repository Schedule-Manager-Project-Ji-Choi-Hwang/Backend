package backend.schedule.entity;

import backend.schedule.enumlist.RepeatedDate;
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

    @Enumerated(EnumType.STRING)
    private RepeatedDate repeatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ps_id")
    private PersonalSubject personalSubject;

    public Schedule(String scheduleName, LocalDate period, RepeatedDate repeatedDate) {
        this.scheduleName = scheduleName;
        this.period = period;
        this.repeatedDate = repeatedDate;
    }

    public void setPersonalSubject(PersonalSubject personalSubject) {
        this.personalSubject = personalSubject;
    }
}
