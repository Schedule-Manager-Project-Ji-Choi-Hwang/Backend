package backend.schedule.repository;

import backend.schedule.entity.StudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Long> {
}
