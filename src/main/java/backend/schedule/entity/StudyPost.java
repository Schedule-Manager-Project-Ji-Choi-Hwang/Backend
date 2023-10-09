package backend.schedule.entity;


import backend.schedule.enumlist.FieldTag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyPost {

    @Id
    @GeneratedValue
    @Column(name = "studypost_id")
    private Long id;

    private String studyName;

    @Enumerated(EnumType.STRING)
    private FieldTag tag;

    private Date period;

    private int recruitMember;

    private boolean onOff;

    private String area; //이거도 나중에 enum으로 바꿀예정

    private String post;

    @OneToMany(mappedBy = "studyPost")
    private List<StudyMember> studyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "studyPost")
    private List<ApplicationMember> applicationMembers = new ArrayList();

    @OneToMany(mappedBy = "studyPost")
    private List<StudySchedule> studySchedules = new ArrayList<>();

    @OneToMany(mappedBy = "studyPost")
    private List<StudyAnnouncement> studyAnnouncements = new ArrayList<>();

    public void addStudyMember(StudyMember studyMember) {
        studyMembers.add(studyMember);
        studyMember.setStudyPost(this);
    }

    public void addApplicationMember(ApplicationMember applicationMember) {
        applicationMembers.add(applicationMember);
        applicationMember.setStudyPost(this);
    }

    public void addStudySchedule(StudySchedule studySchedule) {
        studySchedules.add(studySchedule);
        studySchedule.setStudyPost(this);
    }

    public void addStudyAnnouncements(StudyAnnouncement studyAnnouncement) {
        studyAnnouncements.add(studyAnnouncement);
        studyAnnouncement.setStudyPost(this);
    }


    public StudyPost(String studyName, FieldTag tag, Date period, int recruitMember, boolean onOff, String area, String post) {
        this.studyName = studyName;
        this.tag = tag;
        this.period = period;
        this.recruitMember = recruitMember;
        this.onOff = onOff;
        this.area = area;
        this.post = post;
    }
}
