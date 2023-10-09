package backend.schedule.entity;


import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ApplicationMember {

    @Id
    @GeneratedValue
    @Column(name = "application_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studypost_id")
    private StudyPost studyPost;

    public void setMember(Member member) {
        this.member = member;
    }

    public void setStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
    }
}
