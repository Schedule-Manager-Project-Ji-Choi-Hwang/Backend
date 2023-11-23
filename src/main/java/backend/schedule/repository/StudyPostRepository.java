package backend.schedule.repository;

import backend.schedule.entity.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long>, StudyPostRepositoryCustom {

    @Query("select s from StudyPost s join fetch s.studySchedules sc where s.id = :boardId and sc.period = :date")
    StudyPost DetailPageStudySchedules(@Param("boardId") Long boardId, @Param("date")LocalDate date);
    @Query("select s from StudyPost s join fetch s.studySchedules sc where s.id = :boardId")
    StudyPost studyScheduleList(@Param("boardId") Long boardId);

    @Query("select s from StudyPost s join fetch s.studyAnnouncements sa where s.id = :boardId and sa.id = :id")
    StudyPost studyAnnouncement(@Param("boardId") Long boardId, @Param("id") Long id);

    @Query("select s from StudyPost s join fetch s.studyAnnouncements sa where s.id = :boardId")
    StudyPost studyAnnouncements(@Param("boardId") Long boardId);

    @Query("select s from StudyPost s join fetch s.studySchedules ss where s.id = :boardId")
    List<StudyPost> detailPage(@Param("boardId") Long boardId);
}
