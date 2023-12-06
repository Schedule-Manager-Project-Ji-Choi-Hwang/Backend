package backend.schedule.repository;

import backend.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("select s from Schedule s where s.id = :scheduleId and s.subject.id = :subjectId")
    Optional<Schedule> findSchedule(@Param("scheduleId") Long scheduleId, @Param("subjectId") Long subjectId);
}
