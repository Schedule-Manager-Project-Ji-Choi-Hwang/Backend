package backend.schedule.repository;

import backend.schedule.entity.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long>, StudyPostRepositoryCustom {

    @Query("select s from StudyPost s join fetch s.studyAnnouncements sa where s.id = :studyBoardId and sa.id = :studyAnnouncementId")
    Optional<StudyPost> studyAnnouncement(@Param("studyBoardId") Long studyBoardId, @Param("studyAnnouncementId") Long studyAnnouncementId);

    @Query("select s from StudyPost s join fetch s.applicationMembers am join fetch am.member where s.id = :studyBoardId ")
    Optional<StudyPost> findStudyPostByApplicationMembers(@Param("studyBoardId") Long studyBoardId);

    @Query("select sp from StudyPost sp join fetch sp.studyMembers sm join fetch sm.member where sp.id = :studyBoardId")
    Optional<StudyPost> findStudyPostGetStudyMembers(@Param("studyBoardId") Long studyBoardId);

    @Query("select s from StudyPost s join fetch s.studyAnnouncements sa where s.id = :studyBoardId")
    Optional<StudyPost> studyAnnouncements(@Param("studyBoardId") Long studyBoardId); //이거

    @Query("select s from StudyPost s join fetch s.studySchedules ss where s.id = :studyBoardId")
    List<StudyPost> detailPage(@Param("studyBoardId") Long studyBoardId);

//    @Query("select s from StudyPost s join fetch s.studySchedules sc where s.id = :boardId")
//    Optional<StudyPost> studyScheduleList(@Param("boardId") Long boardId);

//    @Query("select s from StudyPost s join fetch s.studySchedules sc where s.id = :studyBoardId and sc.period = :date")
//    StudyPost DetailPageStudySchedules(@Param("studyBoardId") Long studyBoardId, @Param("date")LocalDate date);
}
