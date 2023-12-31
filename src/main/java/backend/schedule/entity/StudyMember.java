package backend.schedule.entity;


import backend.schedule.enumlist.ConfirmAuthor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studymember_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studypost_id")
    private StudyPost studyPost;

    @Enumerated(EnumType.STRING)
    private ConfirmAuthor confirmAuthor;

    public StudyMember(ConfirmAuthor confirmAuthor) {
        this.confirmAuthor = confirmAuthor;
    }

    public StudyMember(Member member, ConfirmAuthor confirmAuthor) {
        this.member = member;
        this.confirmAuthor = confirmAuthor;
    }

    public void setStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
    }

    public void changeLeader() {
        this.confirmAuthor = ConfirmAuthor.LEADER;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
