package backend.schedule.service;


import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyAnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyAnnouncementService {

    private final StudyAnnouncementRepository studyAnnouncementRepository;

    public Long save(StudyAnnouncement studyAnnouncement) {
        return studyAnnouncementRepository.save(studyAnnouncement).getId();
    }

    public StudyAnnouncement findById(Long id) {
        Optional<StudyAnnouncement> optionalStudyAnnouncement = studyAnnouncementRepository.findById(id);

        return optionalStudyAnnouncement.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.ANNOUNCEMENT));
    }

    public int removeAnnouncement(Long studyBoardId, Long announcementId) {
        return studyAnnouncementRepository.removeAnnouncement(studyBoardId, announcementId);

//        if (removeAnnouncement == 1) {
//            return ErrorMessage.DELETE;
//        } else {
//            return new IllegalArgumentException(ErrorMessage.NOTDELETE);
//        }
    }

    public void delete(Long id) {
        studyAnnouncementRepository.deleteById(id);
    }

    public StudyAnnouncement announcementCommentList(Long id) {
        Optional<StudyAnnouncement> optionalStudyAnnouncement = studyAnnouncementRepository.announcementCommentList(id);

        return optionalStudyAnnouncement.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.ANNOUNCEMENT));
    }
}
