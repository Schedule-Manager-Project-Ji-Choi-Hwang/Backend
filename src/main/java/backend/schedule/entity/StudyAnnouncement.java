package backend.schedule.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyAnnouncement {

    @Id
    @GeneratedValue
    @Column(name = "studyannouncement_id")
    private Long id;

    private String announcementTitle;

    private String announcementPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studypost_id")
    private StudyPost studyPost;

    @OneToMany(mappedBy = "studyAnnouncement")
    private List<StudyComment> studyComments = new ArrayList<>();

    public void addStudyComment(StudyComment studyComment) {
        studyComments.add(studyComment);
        studyComment.setStudyAnnouncement(this);
    }

    public StudyAnnouncement(String announcementTitle, String announcementPost) {
        this.announcementTitle = announcementTitle;
        this.announcementPost = announcementPost;
    }

    public void setStudyPost(StudyPost studyPost) {
        this.studyPost = studyPost;
    }
}
