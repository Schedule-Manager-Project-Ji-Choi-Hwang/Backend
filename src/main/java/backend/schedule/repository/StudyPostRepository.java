package backend.schedule.repository;

import backend.schedule.entity.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {

    @Query("select s from StudyPost s join fetch s.studySchedules sc where s.id = :boardId")
    StudyPost studyScheduleList(@Param("boardId") Long boardId);
}
