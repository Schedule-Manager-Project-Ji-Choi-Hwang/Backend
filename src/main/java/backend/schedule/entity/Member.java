package backend.schedule.entity;


import backend.schedule.dto.member.MemberJoinReqDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String password;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<Subject> subjects = new ArrayList<>();

//    @OneToMany(mappedBy = "member")
//    private List<StudyMember> studyMembers = new ArrayList<>();

//    @OneToMany(mappedBy = "member")
//    private List<ApplicationMember> applicationMembers = new ArrayList();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    private List<StudyComment> studyComments = new ArrayList<>();

    public void addPersonalSubject(Subject subject) {
        subjects.add(subject);
        subject.setMember(this);
    }

    public void addStudyComments(StudyComment studyComment) {
        studyComments.add(studyComment);
        studyComment.setMember(this);
    }

//    public void addStudyMember(StudyMember studyMember) {
//        studyMembers.add(studyMember);
//        studyMember.setMember(this);

//    }
//    public void addApplicationMember(ApplicationMember applicationMember) {
//        applicationMembers.add(applicationMember);
//        applicationMember.setMember(this);

//    }

    public Member(MemberJoinReqDto memberJoinReqDto, String encodedPassword) {
        this.loginId = memberJoinReqDto.getLoginId();
        this.password = encodedPassword;
        this.nickname = memberJoinReqDto.getNickname();
        this.email = memberJoinReqDto.getEmail();
    }

    public Member(String loginId, String encodedPassword, String nickname, String email) {
        this.loginId = loginId;
        this.password = encodedPassword;
        this.nickname = nickname;
        this.email = email;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
