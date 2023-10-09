package backend.schedule.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyComment {

    @Id
    @GeneratedValue
    @Column(name = "studycommnet_id")
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
