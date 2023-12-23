package backend.schedule.entity;


import backend.schedule.dto.studycomment.StudyCommentDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studycommnet_id", updatable = false)
    private Long id;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyannouncement_id")
    private StudyAnnouncement studyAnnouncement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public StudyComment(StudyCommentDto commentDto) {
        this.comment = commentDto.getComment();
    }

    public StudyComment(String comment) {
        this.comment = comment;
    }

    public void commentUpdate(StudyCommentDto commentDto) {
        this.comment = commentDto.getComment();
    }

    public void setStudyAnnouncement(StudyAnnouncement studyAnnouncement) {
        this.studyAnnouncement = studyAnnouncement;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
