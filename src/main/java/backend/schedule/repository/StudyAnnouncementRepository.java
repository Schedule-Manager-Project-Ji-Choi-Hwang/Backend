package backend.schedule.repository;

import backend.schedule.entity.StudyAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyAnnouncementRepository extends JpaRepository<StudyAnnouncement, Long> {
}
