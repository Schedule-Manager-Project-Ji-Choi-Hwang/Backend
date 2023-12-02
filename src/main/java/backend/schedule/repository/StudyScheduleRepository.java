package backend.schedule.repository;

import backend.schedule.entity.StudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Long> {

    @Modifying
    @Query("delete from StudySchedule ss where ss.studyPost.id = :studyBoardId and ss.id = :studyScheduleId")
    int removeStudySchedule(@Param("studyBoardId") Long studyBoardId, @Param("studyScheduleId") Long studyScheduleId);

}
