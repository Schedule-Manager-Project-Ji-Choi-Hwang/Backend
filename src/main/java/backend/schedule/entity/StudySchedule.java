package backend.schedule.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudySchedule {

    @Id
    @GeneratedValue
    @Column(name = "studyschedule_id")
    private Long id;

    private String scheduleName;

    private Date period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studypost_id")
    private StudyPost studyPost;

    public StudySchedule(String scheduleName, Date period) {
        this.scheduleName = scheduleName;
        this.period = period;
    }

    public void setStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
    }
}
