package backend.schedule.repository;

import backend.schedule.entity.StudyAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyAnnouncementRepository extends JpaRepository<StudyAnnouncement, Long> {

    @Query("select sa from StudyAnnouncement sa left join fetch sa.studyComments sc left join fetch sc.member where sa.id = :announcementId and sa.studyPost.id = :studyBoardId")
    Optional<StudyAnnouncement> findAnnouncementGetComments(@Param("announcementId") Long announcementId, @Param("studyBoardId") Long studyBoardId);

    @Query("select sa from StudyAnnouncement sa where sa.id = :studyAnnouncementId and sa.studyPost.id = :studyBoardId")
    Optional<StudyAnnouncement> findStudyAnnouncement(@Param("studyAnnouncementId") Long studyAnnouncementId, @Param("studyBoardId") Long studyBoardId);
}
