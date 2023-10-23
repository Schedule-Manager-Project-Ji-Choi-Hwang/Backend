package backend.schedule.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(name = "STUDYCOMMENT_SEQ_GENERATOR",
        sequenceName = "STUDYCOMMENT_SEQ")
public class StudyComment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STUDYCOMMENT_SEQ_GENERATOR")
    @Column(name = "studycommnet_id", updatable = false)
    private Long id;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyannouncement_id")
    private StudyAnnouncement studyAnnouncement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public StudyComment(String comment) {
        this.comment = comment;
    }

    public void setStudyAnnouncement(StudyAnnouncement studyAnnouncement) {
        this.studyAnnouncement = studyAnnouncement;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
