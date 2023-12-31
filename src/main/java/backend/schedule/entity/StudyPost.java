package backend.schedule.entity;


import backend.schedule.dto.studypost.StudyPostDto;
import backend.schedule.enumlist.Area;
import backend.schedule.enumlist.FieldTag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyPost extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studypost_id", updatable = false)
    private Long id;

    private String studyName;

    @Enumerated(EnumType.STRING)
    private FieldTag tag;

    private int recruitMember;

    private boolean onOff;

    @Enumerated(EnumType.STRING)
    private Area area;

    @Lob
    private String post;

    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.REMOVE)
    private List<StudyMember> studyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.REMOVE)
    private List<ApplicationMember> applicationMembers = new ArrayList<>();

    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.REMOVE)
    private List<StudySchedule> studySchedules = new ArrayList<>();

    @OneToMany(mappedBy = "studyPost", cascade = CascadeType.REMOVE)
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

    public StudyPost(StudyPostDto studyPostDto) {
        this.studyName = studyPostDto.getStudyName();
        this.tag = studyPostDto.getTag();
        this.recruitMember = studyPostDto.getRecruitMember();
        this.onOff = studyPostDto.isOnOff();
        this.area = studyPostDto.getArea();
        this.post = studyPostDto.getPost();
    }

    public StudyPost(String studyName) {
        this.studyName = studyName;
    }

    public void updatePost(StudyPostDto studyPostDto) {
        this.studyName = studyPostDto.getStudyName();
        this.tag = studyPostDto.getTag();
        this.recruitMember = studyPostDto.getRecruitMember();
        this.onOff = studyPostDto.isOnOff();
        this.area = studyPostDto.getArea();
        this.post = studyPostDto.getPost();
    }

    public StudyPost(String studyName, FieldTag tag, int recruitMember, boolean onOff, Area area, String post) {
        this.studyName = studyName;
        this.tag = tag;
        this.recruitMember = recruitMember;
        this.onOff = onOff;
        this.area = area;
        this.post = post;
    }
}
