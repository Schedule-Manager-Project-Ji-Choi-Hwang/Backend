package backend.schedule.entity;


import backend.schedule.dto.StudyAnnouncementDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SequenceGenerator(name = "STUDYANNOUNCEMENT_SEQ_GENERATOR",
        sequenceName = "STUDYANNOUNCEMENT_SEQ")
public class StudyAnnouncement extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STUDYANNOUNCEMENT_SEQ_GENERATOR")
    @Column(name = "studyannouncement_id", updatable = false)
    private Long id;

    private String announcementTitle;

    private String announcementPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studypost_id")
    private StudyPost studyPost;

    @OneToMany(mappedBy = "studyAnnouncement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyComment> studyComments = new ArrayList<>();

    public void addStudyComment(StudyComment studyComment) {
        studyComments.add(studyComment);
        studyComment.setStudyAnnouncement(this);
    }

    public void removeStudyComment(StudyComment studyComment) {
        studyComments.remove(studyComment);
    }

    public void setStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
    }

    public StudyAnnouncement(StudyAnnouncementDto announcementDto) {
        this.announcementTitle = announcementDto.getAnnouncementTitle();
        this.announcementPost = announcementDto.getAnnouncementPost();
    }

    public void announcementUpdate(StudyAnnouncementDto announcementDto) {
        this.announcementTitle = announcementDto.getAnnouncementTitle();
        this.announcementPost = announcementDto.getAnnouncementPost();
    }
}
