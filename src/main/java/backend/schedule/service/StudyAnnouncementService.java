package backend.schedule.service;


import backend.schedule.entity.StudyAnnouncement;
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

        return optionalStudyAnnouncement.orElse(null);
    }

    public void delete(Long id) {
        studyAnnouncementRepository.deleteById(id);
    }

    public StudyAnnouncement announcementCommentList(Long id) {
        return studyAnnouncementRepository.announcementCommentList(id);
    }
}
