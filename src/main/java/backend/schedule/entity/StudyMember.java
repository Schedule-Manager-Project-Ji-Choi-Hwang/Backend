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
    @GeneratedValue
    @Column(name = "studymember_id")
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

    public void setMember(Member member) {
        this.member = member;
    }

    public void setStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
    }
}
