package backend.schedule.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(name = "PERSONALSUBJECT_SEQ_GENERATOR",
        sequenceName = "PERSONALSUBJECT_SEQ")
public class PersonalSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSONALSUBJECT_SEQ_GENERATOR")
    @Column(name = "ps_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String subjectName;

    @OneToMany(mappedBy = "personalSubject")
    private List<Schedule> schedules = new ArrayList<>();

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setPersonalSubject(this);
    }

    public PersonalSubject(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
