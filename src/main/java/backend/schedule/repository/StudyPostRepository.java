package backend.schedule.repository;

import backend.schedule.entity.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyPostRepository extends JpaRepository<StudyPost, Long> {
}
