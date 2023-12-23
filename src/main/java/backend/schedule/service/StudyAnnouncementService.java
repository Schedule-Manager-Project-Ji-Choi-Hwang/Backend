package backend.schedule.service;


import backend.schedule.dto.studyannouncement.AnnouncementAndCommentsDto;
import backend.schedule.dto.studyannouncement.StudyAnnouncementDto;
import backend.schedule.dto.studyannouncement.StudyAnnouncementEditDto;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyAnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyAnnouncementService {

    private final StudyAnnouncementRepository studyAnnouncementRepository;

    @Transactional
    public Long save(StudyPost studyPost, StudyAnnouncementDto announcementDto) {
        StudyAnnouncement announcement = new StudyAnnouncement(announcementDto);
        studyPost.addStudyAnnouncements(announcement);
        return studyAnnouncementRepository.save(announcement).getId();
    }

    public StudyAnnouncement findStudyAnnouncement(Long studyAnnouncementId, Long StudyBoardId) {
        Optional<StudyAnnouncement> optionalStudyAnnouncement = studyAnnouncementRepository.findStudyAnnouncement(studyAnnouncementId, StudyBoardId);

        return optionalStudyAnnouncement.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.ANNOUNCEMENT));
    }

    @Transactional
    public void removeAnnouncement(StudyAnnouncement studyAnnouncement) {
        studyAnnouncementRepository.delete(studyAnnouncement);
    }

    @Transactional
    public void updateStudyAnnouncement(StudyAnnouncement studyAnnouncement, StudyAnnouncementEditDto studyAnnouncementEditDto) {
        studyAnnouncement.announcementUpdate(studyAnnouncementEditDto);
    }

    public void delete(Long id) {
        studyAnnouncementRepository.deleteById(id);
    }

    public StudyAnnouncement announcementCommentList(Long announcementId, Long studyBoardId) {
        Optional<StudyAnnouncement> optionalStudyAnnouncement = studyAnnouncementRepository.findAnnouncementGetComments(announcementId, studyBoardId);

        return optionalStudyAnnouncement.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.ANNOUNCEMENT));
    }

    public AnnouncementAndCommentsDto returnAnnouncementAndComments(StudyAnnouncement studyAnnouncement) {
        return new AnnouncementAndCommentsDto(studyAnnouncement);
    }
}
