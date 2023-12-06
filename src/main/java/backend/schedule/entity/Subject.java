package backend.schedule.entity;


import backend.schedule.dto.subject.SubjectReqDto;
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
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSONALSUBJECT_SEQ_GENERATOR")
    @Column(name = "ps_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String subjectName;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.REMOVE)
    private List<Schedule> schedules = new ArrayList<>();

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setSubject(this);
    }

    public Subject(String subjectName) {
        this.subjectName = subjectName;
    }

    public void removeSchedule(Schedule schedule) {
        schedules.remove(schedule);
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void subjectNameUpdate(SubjectReqDto subjectReqDto) {
        this.subjectName = subjectReqDto.getSubjectName();
    }

    public void addSchedules(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.setSubject(this);
    }

    public Subject(Member member, String subjectName) {
        this.member = member;
        this.subjectName = subjectName;
    }
}
