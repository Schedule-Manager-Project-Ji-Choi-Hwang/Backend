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
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ps_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String subjectName;

    private String color;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.REMOVE)
    private List<Schedule> schedules = new ArrayList<>();

    public void addSchedules(Schedule schedule) {
        schedules.add(schedule);
        schedule.setSubject(this);
    }

    public Subject(Member member, String subjectName, String color) {
        this.member = member;
        this.subjectName = subjectName;
        this.color = color;
    }

    public Subject(SubjectReqDto subjectReqDto) {
        this.subjectName = subjectReqDto.getSubjectName();
        this.color = subjectReqDto.getColor();
    }


    public void setMember(Member member) {
        this.member = member;
    }

    public void subjectNameUpdate(SubjectReqDto subjectReqDto) {
        this.subjectName = subjectReqDto.getSubjectName();
        this.color = subjectReqDto.getColor();
    }
}
