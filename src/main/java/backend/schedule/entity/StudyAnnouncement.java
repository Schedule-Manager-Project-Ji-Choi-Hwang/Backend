package backend.schedule.entity;


import backend.schedule.dto.studyannouncement.StudyAnnouncementDto;
import backend.schedule.dto.studyannouncement.StudyAnnouncementEditDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyAnnouncement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyannouncement_id", updatable = false)
    private Long id;

    private String announcementTitle;

    private String announcementPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studypost_id")
    private StudyPost studyPost;

    @OneToMany(mappedBy = "studyAnnouncement", cascade = CascadeType.REMOVE)
    private List<StudyComment> studyComments = new ArrayList<>();

    public StudyAnnouncement(StudyAnnouncementDto announcementDto) {
        this.announcementTitle = announcementDto.getAnnouncementTitle();
        this.announcementPost = announcementDto.getAnnouncementPost();
    }

    public StudyAnnouncement(String announcementTitle, String announcementPost) {
        this.announcementTitle = announcementTitle;
        this.announcementPost = announcementPost;
    }

    public void addStudyComment(StudyComment studyComment) {
        studyComments.add(studyComment);
        studyComment.setStudyAnnouncement(this);
    }

    public void announcementUpdate(StudyAnnouncementEditDto studyAnnouncementEditDto) {
        this.announcementPost = studyAnnouncementEditDto.getAnnouncementPost();
    }

    public void setStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
    }
}
