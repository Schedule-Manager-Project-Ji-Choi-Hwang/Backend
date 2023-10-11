package backend.schedule.entity;


import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(name = "APPLICATIONMEMBER_SEQ_GENERATOR",
        sequenceName = "APPLICATIONMEMBER_SEQ")
public class ApplicationMember {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APPLICATIONMEMBER_SEQ_GENERATOR")
    @Column(name = "application_id", updatable = false)
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
