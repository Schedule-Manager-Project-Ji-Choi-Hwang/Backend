package backend.schedule.entity;

import backend.schedule.enumlist.RepeatedDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Schedule {

    @Id
    @GeneratedValue
    @Column(name = "schedule_id")
    private Long id;

    private String scheduleName;

    private Date period;

    @Enumerated(EnumType.STRING)
    private RepeatedDate repeatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ps_id")
    private PersonalSubject personalSubject;

    public Schedule(String scheduleName, Date period, RepeatedDate repeatedDate) {
        this.scheduleName = scheduleName;
        this.period = period;
        this.repeatedDate = repeatedDate;
    }

    public void setPersonalSubject(PersonalSubject personalSubject) {
        this.personalSubject = personalSubject;
    }
}
