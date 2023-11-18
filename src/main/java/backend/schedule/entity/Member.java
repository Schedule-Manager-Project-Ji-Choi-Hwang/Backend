package backend.schedule.entity;


import backend.schedule.dto.MemberJoinReqDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@SequenceGenerator(name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ")
public class Member extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_SEQ_GENERATOR")
    @Column(name = "member_id", updatable = false)
    private Long id;

    private String loginId;

    private String password;

    private String nickname;

    private String email;

    @OneToMany(mappedBy = "member")
    private List<PersonalSubject> personalSubjects = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<StudyMember> studyMembers = new ArrayList<>();

//    @OneToMany(mappedBy = "member")
//    private List<ApplicationMember> applicationMembers = new ArrayList();

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

//    public void addApplicationMember(ApplicationMember applicationMember) {
//        applicationMembers.add(applicationMember);
//        applicationMember.setMember(this);
//    }

    public void addStudyComments(StudyComment studyComment) {
        studyComments.add(studyComment);
        studyComment.setMember(this);
    }

    public void removeSubject(PersonalSubject personalSubject) {
        personalSubjects.remove(personalSubject);
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public Member(MemberJoinReqDto memberJoinReqDto, String encodedPassword) {
        this.loginId = memberJoinReqDto.getLoginId();
        this.password = encodedPassword;
        this.nickname = memberJoinReqDto.getNickname();
        this.email = memberJoinReqDto.getEmail();
    }
}
