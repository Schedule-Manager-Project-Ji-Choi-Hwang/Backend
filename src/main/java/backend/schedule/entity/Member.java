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
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String loginId;

    private String password;

    private String nickname;

    private String email;

    @OneToMany(mappedBy = "member")
    private List<PersonalSubject> personalSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<StudyMember> studyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<ApplicationMember> applicationMembers = new ArrayList();

    @OneToMany(mappedBy = "member")
    private List<StudyComment> studyComments = new ArrayList<>();

    public void addPersonalSubject(PersonalSubject personalSubject) {
        personalSubjects.add(personalSubject);
        personalSubject.setMember(this);
    }

    public void addStudyMember(StudyMember studyMember) {
        studyMembers.add(studyMember);
        studyMember.setMember(this);
    }

    public void addApplicationMember(ApplicationMember applicationMember) {
        applicationMembers.add(applicationMember);
        applicationMember.setMember(this);
    }

    public void addStudyComments(StudyComment studyComment) {
        studyComments.add(studyComment);
        studyComment.setMember(this);
    }

    public Member(String loginId, String password, String nickname, String email) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }


}
