package backend.schedule.repository;

import backend.schedule.entity.StudyAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyAnnouncementRepository extends JpaRepository<StudyAnnouncement, Long> {

    @Query("select sa from StudyAnnouncement sa join fetch sa.studyComments sc where sa.id = :id")
    Optional<StudyAnnouncement> announcementCommentList(@Param("id") Long id);
}
