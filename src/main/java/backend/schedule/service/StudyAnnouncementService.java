package backend.schedule.service;


import backend.schedule.dto.StudyAnnouncementDto;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.repository.StudyAnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyAnnouncementService {

    private final StudyAnnouncementRepository studyAnnouncementRepository;

    public void save(StudyAnnouncementDto announcementDto) {
        StudyAnnouncement announcement = new StudyAnnouncement(announcementDto);
        studyAnnouncementRepository.save(announcement);
    }

    public Optional<StudyAnnouncement> findById(Long id) {
        return studyAnnouncementRepository.findById(id);
    }

    public void delete(Long id) {
        studyAnnouncementRepository.deleteById(id);
    }
}
